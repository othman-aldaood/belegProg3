package domainLogic;

import administration.Customer;
import cargo.Cargo;
import cargo.Hazard;
import events.CapacityObserver;
import events.CargoCommandListener;
import events.ChangeObserver;
import events.GLFeedbackListener;
import events.HazardObserver;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * Diese Klasse verwaltet das Warenlager, die Kunden und die Frachtstücke.
 * Die Implementierung verzichtet vollständig auf 'instanceof' und Down-Casting.
 * Angepasst für JOS und JBP (JavaBeans Konvention).
 */
public class WarehouseManager implements CargoCommandListener, Serializable {

    private static final long serialVersionUID = 1L;

    // Felder dürfen NICHT final sein (Wichtig für JBP / XMLDecoder)
    private int capacity;
    private int nextLocation = 1;

    private Set<Customer> customers = new HashSet<>();
    private Map<Integer, Cargo> cargos = new HashMap<>();
    private Map<Integer, Customer> cargoOwners = new HashMap<>();
    private Map<Integer, String> cargoTypes = new HashMap<>();
    private Map<Integer, Date> insertionDates = new HashMap<>();
    private Map<Integer, Date> inspectionDates = new HashMap<>();

    // Die Listener sind transient: sie gehören nicht zum Zustand der GL und
    // sind nicht serialisierbar.
    // Beobachter werden als Listen verwaltet (Observer-Pattern mit An-/Abmeldung).
    private transient GLFeedbackListener feedbackListener;
    private transient List<CapacityObserver> capacityObservers = new ArrayList<>();
    private transient List<HazardObserver> hazardObservers = new ArrayList<>();
    private transient List<ChangeObserver> changeObservers = new ArrayList<>();

    /**
     * Standardkonstruktor (Zwingend erforderlich für JBP).
     */
    public WarehouseManager() {
        this(100);
    }

    public WarehouseManager(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Konstruktor zur Wiederherstellung eines gespeicherten Zustands (z.B. durch
     * die Persistenzschicht). Alle Übergaben werden defensiv kopiert, damit die
     * Kapselung der Geschäftslogik erhalten bleibt.
     */
    public WarehouseManager(int capacity, int nextLocation, Set<Customer> customers,
                            Map<Integer, Cargo> cargos, Map<Integer, Customer> cargoOwners,
                            Map<Integer, String> cargoTypes, Map<Integer, Date> insertionDates,
                            Map<Integer, Date> inspectionDates) {
        this.capacity = capacity;
        this.nextLocation = nextLocation;
        this.customers = customers == null ? new HashSet<>() : new HashSet<>(customers);
        this.cargos = cargos == null ? new HashMap<>() : new HashMap<>(cargos);
        this.cargoOwners = cargoOwners == null ? new HashMap<>() : new HashMap<>(cargoOwners);
        this.cargoTypes = cargoTypes == null ? new HashMap<>() : new HashMap<>(cargoTypes);
        this.insertionDates = insertionDates == null ? new HashMap<>() : new HashMap<>(insertionDates);
        this.inspectionDates = inspectionDates == null ? new HashMap<>() : new HashMap<>(inspectionDates);
    }

    /**
     * Initialisiert die transienten Beobachterlisten nach dem Laden via JOS
     * (Feldinitialisierer laufen bei der Deserialisierung nicht).
     * Die Beobachter selbst gehören laut Anforderung nicht zum Zustand und
     * müssen nach dem Laden nicht wieder eingehangen werden.
     */
    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.capacityObservers = new ArrayList<>();
        this.hazardObservers = new ArrayList<>();
        this.changeObservers = new ArrayList<>();
    }

    // =========================================================================
    // --- Zustandsabfragen ---
    // Alle Abfragen liefern ausschließlich defensive Kopien (Kapselung).
    // Es gibt keine öffentlichen Setter: der Zustand ist nur über die
    // Kommandos bzw. den Wiederherstellungs-Konstruktor veränderbar.
    // =========================================================================

    public int getCapacity() {
        return this.capacity;
    }

    public int getNextLocation() {
        return this.nextLocation;
    }

    public synchronized Map<Integer, String> getCargoTypesMap() {
        return new HashMap<>(this.cargoTypes);
    }


    // =========================================================================
    // --- Logik und Funktionalitäten ---
    // =========================================================================

    public void setFeedbackListener(GLFeedbackListener feedbackListener) {
        this.feedbackListener = feedbackListener;
    }

    public synchronized void addCapacityObserver(CapacityObserver observer) {
        this.capacityObservers.add(observer);
    }

    public synchronized void removeCapacityObserver(CapacityObserver observer) {
        this.capacityObservers.remove(observer);
    }

    public synchronized void addHazardObserver(HazardObserver observer) {
        this.hazardObservers.add(observer);
    }

    public synchronized void removeHazardObserver(HazardObserver observer) {
        this.hazardObservers.remove(observer);
    }

    public synchronized void addChangeObserver(ChangeObserver observer) {
        this.changeObservers.add(observer);
    }

    public synchronized void removeChangeObserver(ChangeObserver observer) {
        this.changeObservers.remove(observer);
    }

    private void sendFeedback(String message) {
        // Kein System.out in der GL: ohne registrierten Listener keine Ausgabe.
        if (feedbackListener != null) {
            feedbackListener.onFeedbackReceived(message);
        }
    }

    @Override
    public synchronized void onInsertCustomer(String customerName) {
        if (customerName == null) {
            sendFeedback("Fehler: Kein Kundenname angegeben.");
            return;
        }
        Customer newCustomer = new CustomerImpl(customerName);
        boolean added = this.customers.add(newCustomer);
        if (added) {
            sendFeedback("Erfolg: Kunde '" + customerName + "' wurde angelegt.");
            notifyChange();
        } else {
            sendFeedback("Fehler: Kunde '" + customerName + "' existiert bereits.");
        }
    }

    @Override
    public synchronized void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
        if (this.cargos.size() >= this.capacity) {
            sendFeedback("Fehler: Das Lager ist voll!");
            return;
        }

        if (this.cargos.size() >= (this.capacity * 0.9)) {
            // Beobachter erhalten nur das Signal, keinen Inhalt.
            for (CapacityObserver observer : this.capacityObservers) {
                observer.onCapacityWarning();
            }
        }

        Customer owner = findCustomer(customerName);

        if (owner == null) {
            sendFeedback("Fehler: Kunde '" + customerName + "' nicht gefunden.");
            return;
        }

        Collection<Hazard> hazardEnums = new ArrayList<>();
        if (hazards != null) {
            for (String h : hazards) {
                try {
                    hazardEnums.add(Hazard.valueOf(h.toUpperCase().trim()));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        Cargo newCargo;
        if ("DryBulkCargo".equals(type)) {
            newCargo = new DryBulkCargoImpl(owner, value, hazardEnums, grainSize);
        } else if ("UnitisedCargo".equals(type)) {
            newCargo = new UnitisedCargoImpl(owner, value, hazardEnums, isFragile);
        } else if ("DryBulkAndUnitisedCargo".equals(type)) {
            newCargo = new DryBulkAndUnitisedCargoImpl(owner, value, hazardEnums, grainSize, isFragile);
        } else {
            sendFeedback("Fehler: Unbekannter Frachttyp.");
            return;
        }

        Set<Hazard> hazardsBefore = presentHazards();
        int currentLocation = this.nextLocation++;
        this.cargos.put(currentLocation, newCargo);
        this.cargoOwners.put(currentLocation, owner);
        this.cargoTypes.put(currentLocation, type);
        this.insertionDates.put(currentLocation, new Date());
        this.inspectionDates.put(currentLocation, new Date());

        sendFeedback("Erfolg: " + type + " auf Lagerplatz " + currentLocation + " eingefügt.");
        notifyChange();
        notifyHazardChange(hazardsBefore);
    }

    @Override
    public synchronized void onReadCustomers() {
        StringBuilder sb = new StringBuilder("Kunden:\n");
        for (Customer c : this.customers) {
            long count = this.cargoOwners.values().stream().filter(owner -> owner.equals(c)).count();
            sb.append("- ").append(c.getName()).append(" (").append(count).append(")\n");
        }
        sendFeedback(sb.toString().trim());
    }

    @Override
    public synchronized void onReadCargos(String cargoType) {
        // Anzeige mit Platz, Inspektionsdatum und Einlagerungsdauer in Sekunden (laut Anforderung)
        StringBuilder sb = new StringBuilder("Frachtstücke:\n");
        for (Map.Entry<Integer, Cargo> entry : this.cargos.entrySet()) {
            int loc = entry.getKey();
            String type = this.cargoTypes.get(loc);
            if (cargoType == null || cargoType.isEmpty() || type.equals(cargoType)) {
                Date insertion = this.insertionDates.get(loc);
                long seconds = insertion == null ? 0 : (new Date().getTime() - insertion.getTime()) / 1000;
                sb.append("Platz ").append(loc)
                        .append(": ").append(type)
                        .append(", Inspektion: ").append(this.inspectionDates.get(loc))
                        .append(", Lagerdauer: ").append(seconds).append("s\n");
            }
        }
        sendFeedback(sb.toString().trim());
    }

    @Override
    public synchronized void onReadHazards(boolean existing) {
        Set<Hazard> present = presentHazards();

        Set<Hazard> result;
        if (existing) {
            result = present;
        } else {
            result = new HashSet<>(Arrays.asList(Hazard.values()));
            result.removeAll(present);
        }

        StringBuilder sb = new StringBuilder(existing
                ? "Vorhandene Gefahrenstoffe:" : "Nicht vorhandene Gefahrenstoffe:");
        if (result.isEmpty()) {
            sb.append(" keine");
        }
        for (Hazard h : result) {
            sb.append(" ").append(h);
        }
        sendFeedback(sb.toString());
    }

    @Override
    public synchronized void onUpdateInspectionDate(int storageLocation) {
        Cargo cargo = this.cargos.get(storageLocation);
        if (cargo != null) {
            cargo.setLastInspectionDate(new Date());
            this.inspectionDates.put(storageLocation, new Date());
            sendFeedback("Erfolg: Inspektionsdatum aktualisiert.");
            notifyChange();
        } else {
            sendFeedback("Fehler: Frachtstück nicht gefunden.");
        }
    }

    @Override
    public synchronized void onDeleteCustomer(String customerName) {
        Customer target = findCustomer(customerName);
        if (target == null) {
            sendFeedback("Fehler: Kunde '" + customerName + "' nicht gefunden.");
            return;
        }

        // Frachtstücke der Kundin entfernen (jedes Frachtstück muss zu einer
        // existierenden Kundin gehören, daher dürfen keine verwaisten bleiben)
        List<Integer> locations = new ArrayList<>();
        for (Map.Entry<Integer, Customer> entry : this.cargoOwners.entrySet()) {
            if (entry.getValue().equals(target)) {
                locations.add(entry.getKey());
            }
        }
        Set<Hazard> hazardsBefore = presentHazards();
        for (Integer loc : locations) {
            this.cargos.remove(loc);
            this.cargoOwners.remove(loc);
            this.cargoTypes.remove(loc);
            this.insertionDates.remove(loc);
            this.inspectionDates.remove(loc);
        }

        this.customers.remove(target);
        sendFeedback("Erfolg: Kunde '" + customerName + "' und " + locations.size()
                + " Frachtstueck(e) geloescht.");
        notifyChange();
        notifyHazardChange(hazardsBefore);
    }

    /**
     * Sucht eine Kundin anhand des Namens.
     */
    private Customer findCustomer(String customerName) {
        for (Customer c : this.customers) {
            if (c.getName().equals(customerName)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public synchronized void onDeleteCargo(int storageLocation) {
        if (this.cargos.containsKey(storageLocation)) {
            Set<Hazard> hazardsBefore = presentHazards();
            this.cargos.remove(storageLocation);
            this.cargoOwners.remove(storageLocation);
            this.cargoTypes.remove(storageLocation);
            this.insertionDates.remove(storageLocation);
            this.inspectionDates.remove(storageLocation);
            sendFeedback("Erfolg: Frachtstück auf Platz " + storageLocation + " gelöscht.");
            notifyChange();
            notifyHazardChange(hazardsBefore);
        } else {
            sendFeedback("Fehler: Frachtstück nicht gefunden.");
        }
    }

    public synchronized Set<Customer> getAllCustomers() {
        // Tiefe Kopien: Aenderungen an den zurueckgegebenen Objekten duerfen
        // die Geschaeftslogik nicht erreichen (Kapselung).
        Set<Customer> copies = new HashSet<>();
        for (Customer c : this.customers) {
            copies.add(new CustomerImpl(c.getName()));
        }
        return copies;
    }

    public synchronized Collection<Cargo> getAllCargos() {
        // Tiefe Kopien inkl. des vergebenen Lagerplatzes (Kapselung).
        List<Cargo> copies = new ArrayList<>();
        for (Map.Entry<Integer, Cargo> entry : this.cargos.entrySet()) {
            copies.add(entry.getValue().copy(entry.getKey()));
        }
        return copies;
    }

    public synchronized List<Integer> getStorageLocations() {
        return new ArrayList<>(this.cargos.keySet());
    }

    public synchronized boolean isEmpty() {
        return this.cargos.isEmpty();
    }

    public synchronized int getCurrentSize() {
        return this.cargos.size();
    }

    public synchronized int getCargoCountForCustomer(Customer c) {
        return (int) this.cargoOwners.values().stream().filter(owner -> owner.equals(c)).count();
    }

    public synchronized boolean swapLagerplatz(int loc1, int loc2) {
        if (!this.cargos.containsKey(loc1) || !this.cargos.containsKey(loc2)) return false;

        Cargo c1 = this.cargos.get(loc1);
        Cargo c2 = this.cargos.get(loc2);
        this.cargos.put(loc1, c2);
        this.cargos.put(loc2, c1);

        Customer owner1 = this.cargoOwners.get(loc1);
        Customer owner2 = this.cargoOwners.get(loc2);
        this.cargoOwners.put(loc1, owner2);
        this.cargoOwners.put(loc2, owner1);

        String type1 = this.cargoTypes.get(loc1);
        String type2 = this.cargoTypes.get(loc2);
        this.cargoTypes.put(loc1, type2);
        this.cargoTypes.put(loc2, type1);

        Date insert1 = this.insertionDates.get(loc1);
        Date insert2 = this.insertionDates.get(loc2);
        this.insertionDates.put(loc1, insert2);
        this.insertionDates.put(loc2, insert1);

        Date inspect1 = this.inspectionDates.get(loc1);
        Date inspect2 = this.inspectionDates.get(loc2);
        this.inspectionDates.put(loc1, inspect2);
        this.inspectionDates.put(loc2, inspect1);

        notifyChange();
        return true;
    }

    /**
     * Ermittelt die Menge der aktuell im Lager vorhandenen Gefahrenstoffe.
     */
    private Set<Hazard> presentHazards() {
        Set<Hazard> present = new HashSet<>();
        for (Cargo c : this.cargos.values()) {
            present.addAll(c.getHazards());
        }
        return present;
    }

    /**
     * Benachrichtigt alle Aenderungs-Beobachter.
     * Die Beobachter erhalten nur das Signal, keinen Inhalt.
     */
    private void notifyChange() {
        for (ChangeObserver observer : this.changeObservers) {
            observer.onChanged();
        }
    }

    /**
     * Benachrichtigt die Gefahrenstoff-Beobachter, falls sich die Menge der
     * vorhandenen Gefahrenstoffe gegenüber dem übergebenen Stand geändert hat.
     */
    private void notifyHazardChange(Set<Hazard> before) {
        Set<Hazard> after = presentHazards();
        if (after.equals(before)) {
            return;
        }
        // Beobachter erhalten nur das Signal, keinen Inhalt.
        for (HazardObserver observer : this.hazardObservers) {
            observer.onHazardsChanged();
        }
    }

    public synchronized Map<Integer, Cargo> getCargosMap() {
        // Tiefe Kopien inkl. des vergebenen Lagerplatzes (Kapselung).
        Map<Integer, Cargo> copies = new HashMap<>();
        for (Map.Entry<Integer, Cargo> entry : this.cargos.entrySet()) {
            copies.put(entry.getKey(), entry.getValue().copy(entry.getKey()));
        }
        return copies;
    }

    public synchronized Map<Integer, Customer> getCargoOwnersMap() {
        // Tiefe Kopien (Kapselung).
        Map<Integer, Customer> copies = new HashMap<>();
        for (Map.Entry<Integer, Customer> entry : this.cargoOwners.entrySet()) {
            copies.put(entry.getKey(), new CustomerImpl(entry.getValue().getName()));
        }
        return copies;
    }

    public synchronized Map<Integer, Date> getInspectionDatesMap() {
        // Date ist veraenderlich, daher werden auch die Werte kopiert (Kapselung).
        Map<Integer, Date> copies = new HashMap<>();
        for (Map.Entry<Integer, Date> entry : this.inspectionDates.entrySet()) {
            copies.put(entry.getKey(), new Date(entry.getValue().getTime()));
        }
        return copies;
    }

    public synchronized Map<Integer, Date> getInsertionDatesMap() {
        // Date ist veraenderlich, daher werden auch die Werte kopiert (Kapselung).
        Map<Integer, Date> copies = new HashMap<>();
        for (Map.Entry<Integer, Date> entry : this.insertionDates.entrySet()) {
            copies.put(entry.getKey(), new Date(entry.getValue().getTime()));
        }
        return copies;
    }
}