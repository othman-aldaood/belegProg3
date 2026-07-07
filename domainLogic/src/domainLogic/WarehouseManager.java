package domainLogic;

import administration.Customer;
import cargo.Cargo;
import cargo.Hazard;
import events.CapacityObserver;
import events.CargoCommandListener;
import events.GLFeedbackListener;

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

    // Listener MÜSSEN transient sein, damit JOS nicht abstürzt!
    private transient GLFeedbackListener feedbackListener;
    private transient CapacityObserver capacityObserver;

    /**
     * Standardkonstruktor (Zwingend erforderlich für JBP).
     */
    public WarehouseManager() {
        this(100);
    }

    public WarehouseManager(int capacity) {
        this.capacity = capacity;
    }

    // =========================================================================
    // --- JavaBeans Getter und Setter ---
    // Getter liefern defensive Kopien (Kapselung!). JBP funktioniert trotzdem:
    // der XMLEncoder liest nur die Werte, der XMLDecoder befüllt über die Setter.
    // =========================================================================

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getNextLocation() {
        return this.nextLocation;
    }

    public void setNextLocation(int nextLocation) {
        this.nextLocation = nextLocation;
    }

    public Set<Customer> getCustomers() {
        return new HashSet<>(this.customers);
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers == null ? new HashSet<>() : new HashSet<>(customers);
    }

    public Map<Integer, Cargo> getCargos() {
        return new HashMap<>(this.cargos);
    }

    public void setCargos(Map<Integer, Cargo> cargos) {
        this.cargos = cargos == null ? new HashMap<>() : new HashMap<>(cargos);
    }

    public Map<Integer, Customer> getCargoOwners() {
        return new HashMap<>(this.cargoOwners);
    }

    public void setCargoOwners(Map<Integer, Customer> cargoOwners) {
        this.cargoOwners = cargoOwners == null ? new HashMap<>() : new HashMap<>(cargoOwners);
    }

    public Map<Integer, String> getCargoTypes() {
        return new HashMap<>(this.cargoTypes);
    }

    public void setCargoTypes(Map<Integer, String> cargoTypes) {
        this.cargoTypes = cargoTypes == null ? new HashMap<>() : new HashMap<>(cargoTypes);
    }

    public Map<Integer, Date> getInsertionDates() {
        return new HashMap<>(this.insertionDates);
    }

    public void setInsertionDates(Map<Integer, Date> insertionDates) {
        this.insertionDates = insertionDates == null ? new HashMap<>() : new HashMap<>(insertionDates);
    }

    public Map<Integer, Date> getInspectionDates() {
        return new HashMap<>(this.inspectionDates);
    }

    public void setInspectionDates(Map<Integer, Date> inspectionDates) {
        this.inspectionDates = inspectionDates == null ? new HashMap<>() : new HashMap<>(inspectionDates);
    }


    // =========================================================================
    // --- Logik und Funktionalitäten ---
    // =========================================================================

    public void setFeedbackListener(GLFeedbackListener feedbackListener) {
        this.feedbackListener = feedbackListener;
    }

    public void addCapacityObserver(CapacityObserver capacityObserver) {
        this.capacityObserver = capacityObserver;
    }

    private void sendFeedback(String message) {
        if (feedbackListener != null) {
            feedbackListener.onFeedbackReceived(message);
        } else {
            System.out.println(message);
        }
    }

    @Override
    public synchronized void onInsertCustomer(String customerName) {
        Customer newCustomer = new CustomerImpl(customerName);
        boolean added = this.customers.add(newCustomer);
        if (added) {
            sendFeedback("Erfolg: Kunde '" + customerName + "' wurde angelegt.");
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

        if (this.capacityObserver != null && this.cargos.size() >= (this.capacity * 0.9)) {
            this.capacityObserver.onCapacityWarning("Achtung! Lagerkapazität hat 90% erreicht.");
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
        try {
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

            int currentLocation = this.nextLocation++;
            this.cargos.put(currentLocation, newCargo);
            this.cargoOwners.put(currentLocation, owner);
            this.cargoTypes.put(currentLocation, type);
            this.insertionDates.put(currentLocation, new Date());
            this.inspectionDates.put(currentLocation, new Date());

            sendFeedback("Erfolg: " + type + " auf Lagerplatz " + currentLocation + " eingefügt.");
        } catch (Exception e) {
            sendFeedback("Fehler: " + e.getMessage());
        }
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
        Set<Hazard> present = new HashSet<>();
        for (Cargo c : this.cargos.values()) {
            present.addAll(c.getHazards());
        }

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
            this.cargos.remove(storageLocation);
            this.cargoOwners.remove(storageLocation);
            this.cargoTypes.remove(storageLocation);
            this.insertionDates.remove(storageLocation);
            this.inspectionDates.remove(storageLocation);
            sendFeedback("Erfolg: Frachtstück auf Platz " + storageLocation + " gelöscht.");
        } else {
            sendFeedback("Fehler: Frachtstück nicht gefunden.");
        }
    }

    public synchronized Set<Customer> getAllCustomers() {
        return new HashSet<>(this.customers);
    }

    public synchronized Collection<Cargo> getAllCargos() {
        return new ArrayList<>(this.cargos.values());
    }

    public synchronized List<Integer> getStorageLocations() {
        return new ArrayList<>(this.cargos.keySet());
    }

    public synchronized int getOldestInspectionLocation() {
        int oldestLoc = -1;
        Date oldestDate = null;
        for (Map.Entry<Integer, Date> entry : this.inspectionDates.entrySet()) {
            if (oldestDate == null || entry.getValue().before(oldestDate)) {
                oldestDate = entry.getValue();
                oldestLoc = entry.getKey();
            }
        }
        return oldestLoc;
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

        return true;
    }

    public synchronized Map<Integer, Cargo> getCargosMap() {
        return new HashMap<>(this.cargos);
    }

    public synchronized Map<Integer, Customer> getCargoOwnersMap() {
        return new HashMap<>(this.cargoOwners);
    }

    public synchronized Map<Integer, Date> getInspectionDatesMap() {
        return new HashMap<>(this.inspectionDates);
    }

    public synchronized Map<Integer, Date> getInsertionDatesMap() {
        return new HashMap<>(this.insertionDates);
    }
}