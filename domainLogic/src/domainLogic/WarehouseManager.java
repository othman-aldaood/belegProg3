package domainLogic;

import administration.Customer;
import cargo.Cargo;
import cargo.Hazard;
import events.CapacityObserver;
import events.CargoCommandListener;
import events.GLFeedbackListener;

import java.math.BigDecimal;
import java.util.*;

/**
 * Diese Klasse verwaltet das Warenlager, die Kunden und die Frachtstücke.
 * Die Implementierung verzichtet vollständig auf 'instanceof' und Down-Casting.
 * Für Prototyp 3 (Simulation) wurden alle zustandsverändernden Methoden synchronisiert.
 */
public class WarehouseManager implements CargoCommandListener {

    private final int capacity;
    private int nextLocation = 1;

    private final Set<Customer> customers = new HashSet<>();

    private final Map<Integer, Cargo> cargos = new HashMap<>();
    private final Map<Integer, Customer> cargoOwners = new HashMap<>();
    private final Map<Integer, String> cargoTypes = new HashMap<>();
    private final Map<Integer, Date> insertionDates = new HashMap<>();
    private final Map<Integer, Runnable> inspectionUpdaters = new HashMap<>();

    /**
     * Speichert das Inspektionsdatum für den schnellen Zugriff in Simulation 3.
     */
    private final Map<Integer, Date> inspectionDates = new HashMap<>();

    private GLFeedbackListener feedbackListener;
    private CapacityObserver capacityObserver;

    /**
     * Standardkonstruktor mit einer Kapazität von 100.
     */
    public WarehouseManager() {
        this(100);
    }

    /**
     * Konstruktor mit definierbarer Kapazität.
     *
     * @param capacity Die maximale Anzahl der Frachtstücke im Lager.
     */
    public WarehouseManager(int capacity) {
        this.capacity = capacity;
    }

    public void setFeedbackListener(GLFeedbackListener feedbackListener) {
        this.feedbackListener = feedbackListener;
    }

    public void addCapacityObserver(CapacityObserver capacityObserver) {
        this.capacityObserver = capacityObserver;
    }

    /**
     *
     * @param message
     */
    private void sendFeedback(String message) {
        if (feedbackListener != null) {
            feedbackListener.onFeedbackReceived(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     *
     * @param customerName Der Name der Kundin oder des Kunden.
     */
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

    /**
     *
     * @param type          Der Typ des Frachtstücks (z.B. UnitisedCargo).
     * @param customerName  Der Name der zugehörigen Kundin oder des Kunden.
     * @param value         Der Wert des Frachtstücks.
     * @param hazards       Eine Sammlung von Gefahrenstoffen.
     * @param isFragile     Gibt an, ob das Frachtstück zerbrechlich ist.
     * @param isPressurized Gibt an, ob das Frachtstück unter Druck steht.
     * @param grainSize     Die Korngroesse (nur relevant fuer Schuettgut).
     */
    @Override
    public synchronized void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
        if (this.cargos.size() >= this.capacity) {
            sendFeedback("Fehler: Das Lager ist voll!");
            return;
        }

        if (this.capacityObserver != null && this.cargos.size() >= (this.capacity * 0.9)) {
            this.capacityObserver.onCapacityWarning("Achtung! Lagerkapazität hat 90% erreicht.");
        }

        Customer owner = null;
        for (Customer c : this.customers) {
            if (c.getName().equals(customerName)) {
                owner = c;
                break;
            }
        }

        if (owner == null) {
            sendFeedback("Fehler: Kunde '" + customerName + "' nicht gefunden.");
            return;
        }

        Collection<Hazard> hazardEnums = new ArrayList<>();
        if (hazards != null) {
            for (String h : hazards) {
                try {
                    hazardEnums.add(Hazard.valueOf(h.toUpperCase().trim()));
                } catch (IllegalArgumentException e) {
                }
            }
        }

        Cargo newCargo = null;
        Runnable updater = null;

        try {
            if ("DryBulkCargo".equals(type)) {
                DryBulkCargoImpl dbCargo = new DryBulkCargoImpl(owner, value, hazardEnums, grainSize);
                newCargo = dbCargo;
                updater = () -> dbCargo.setLastInspectionDate(new Date());
            } else if ("UnitisedCargo".equals(type)) {
                UnitisedCargoImpl uCargo = new UnitisedCargoImpl(owner, value, hazardEnums, isFragile);
                newCargo = uCargo;
                updater = () -> uCargo.setLastInspectionDate(new Date());
            } else {
                sendFeedback("Fehler: Unbekannter Frachttyp.");
                return;
            }

            int currentLocation = this.nextLocation++;
            this.cargos.put(currentLocation, newCargo);
            this.cargoOwners.put(currentLocation, owner);
            this.cargoTypes.put(currentLocation, type);
            this.insertionDates.put(currentLocation, new Date());
            this.inspectionUpdaters.put(currentLocation, updater);
            this.inspectionDates.put(currentLocation, new Date());

            sendFeedback("Erfolg: " + type + " auf Lagerplatz " + currentLocation + " eingefügt.");
        } catch (Exception e) {
            sendFeedback("Fehler: " + e.getMessage());
        }
    }

    /**
     *
     */
    @Override
    public synchronized void onReadCustomers() {
        StringBuilder sb = new StringBuilder("Kunden:\n");
        for (Customer c : this.customers) {
            long count = this.cargoOwners.values().stream().filter(owner -> owner.equals(c)).count();
            sb.append("- ").append(c.getName()).append(" (").append(count).append(")\n");
        }
        sendFeedback(sb.toString().trim());
    }

    /**
     *
     * @param cargoType Der Typ der Frachtstücke (null für alle Typen).
     */
    @Override
    public synchronized void onReadCargos(String cargoType) {
        StringBuilder sb = new StringBuilder("Frachtstücke:\n");
        for (Map.Entry<Integer, Cargo> entry : this.cargos.entrySet()) {
            int loc = entry.getKey();
            String type = this.cargoTypes.get(loc);
            if (cargoType == null || cargoType.isEmpty() || type.equals(cargoType)) {
                sb.append("Platz ").append(loc).append(": ").append(type).append("\n");
            }
        }
        sendFeedback(sb.toString().trim());
    }

    /**
     *
     * @param existing true für vorhandene ('i'), false für nicht vorhandene ('e').
     */
    @Override
    public synchronized void onReadHazards(boolean existing) {
        sendFeedback("Gefahrenstoffe gelistet.");
    }

    /**
     *
     * @param storageLocation Der Lagerplatz des Frachtstücks.
     */
    @Override
    public synchronized void onUpdateInspectionDate(int storageLocation) {
        Runnable updater = this.inspectionUpdaters.get(storageLocation);
        if (updater != null) {
            updater.run();
            this.inspectionDates.put(storageLocation, new Date());
            sendFeedback("Erfolg: Inspektionsdatum aktualisiert.");
        } else {
            sendFeedback("Fehler: Keine Inspektion möglich.");
        }
    }

    /**
     *
     * @param customerName Der Name der zu löschenden Person.
     */
    @Override
    public synchronized void onDeleteCustomer(String customerName) {
    }

    /**
     *
     * @param storageLocation Der Lagerplatz des zu entfernenden Frachtstücks.
     */
    @Override
    public synchronized void onDeleteCargo(int storageLocation) {
        if (this.cargos.containsKey(storageLocation)) {
            this.cargos.remove(storageLocation);
            this.cargoOwners.remove(storageLocation);
            this.cargoTypes.remove(storageLocation);
            this.insertionDates.remove(storageLocation);
            this.inspectionUpdaters.remove(storageLocation);
            this.inspectionDates.remove(storageLocation);
            sendFeedback("Erfolg: Frachtstück auf Platz " + storageLocation + " gelöscht.");
        } else {
            sendFeedback("Fehler: Frachtstück nicht gefunden.");
        }
    }

    /**
     *
     * @return
     */
    public synchronized Set<Customer> getAllCustomers() {
        return new HashSet<>(this.customers);
    }

    public synchronized Collection<Cargo> getAllCargos() {
        return new ArrayList<>(this.cargos.values());
    }

    public int getCapacity() {
        return this.capacity;
    }

    public synchronized List<Integer> getStorageLocations() {
        return new ArrayList<>(this.cargos.keySet());
    }

    /**
     * Sucht den Lagerplatz mit dem ältesten Inspektionsdatum.
     * Erfüllt die Vorgabe für den modifizierten Konsumenten in Simulation 3.
     *
     * @return Die ID des Lagerplatzes oder -1, wenn das Lager leer ist.
     */
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

    /**
     *
     * @return
     */
    public synchronized boolean isEmpty() {
        return this.cargos.isEmpty();
    }

    /**
     *
     * @return
     */
    public synchronized int getCurrentSize() {
        return this.cargos.size();
    }
    // --- METHODEN FÜR DIE GUI (PROTOTYP 4) ---

    /**
     * Gibt die Anzahl der Frachtstücke für einen bestimmten Kunden zurück.
     */
    public synchronized int getCargoCountForCustomer(Customer c) {
        return (int) this.cargoOwners.values().stream().filter(owner -> owner.equals(c)).count();
    }

    /**
     * Tauscht die Lagerplätze von zwei Frachtstücken (Für Drag & Drop).
     */
    public synchronized boolean swapLagerplatz(int loc1, int loc2) {
        if (!this.cargos.containsKey(loc1) || !this.cargos.containsKey(loc2)) return false;

        // Frachtstücke tauschen
        Cargo c1 = this.cargos.get(loc1);
        Cargo c2 = this.cargos.get(loc2);
        this.cargos.put(loc1, c2);
        this.cargos.put(loc2, c1);

        // Besitzer tauschen
        Customer owner1 = this.cargoOwners.get(loc1);
        Customer owner2 = this.cargoOwners.get(loc2);
        this.cargoOwners.put(loc1, owner2);
        this.cargoOwners.put(loc2, owner1);

        // Typen tauschen
        String type1 = this.cargoTypes.get(loc1);
        String type2 = this.cargoTypes.get(loc2);
        this.cargoTypes.put(loc1, type2);
        this.cargoTypes.put(loc2, type1);

        // Einfügedaten tauschen
        Date insert1 = this.insertionDates.get(loc1);
        Date insert2 = this.insertionDates.get(loc2);
        this.insertionDates.put(loc1, insert2);
        this.insertionDates.put(loc2, insert1);

        // Inspektions-Updater tauschen
        Runnable updater1 = this.inspectionUpdaters.get(loc1);
        Runnable updater2 = this.inspectionUpdaters.get(loc2);
        this.inspectionUpdaters.put(loc1, updater2);
        this.inspectionUpdaters.put(loc2, updater1);

        // Inspektionsdaten tauschen
        Date inspect1 = this.inspectionDates.get(loc1);
        Date inspect2 = this.inspectionDates.get(loc2);
        this.inspectionDates.put(loc1, inspect2);
        this.inspectionDates.put(loc2, inspect1);

        return true;
    }

    // Getter für die GUI, um die Tabellen zu füllen
    public synchronized Map<Integer, Cargo> getCargosMap() { return new HashMap<>(this.cargos); }
    public synchronized Map<Integer, Customer> getCargoOwnersMap() { return new HashMap<>(this.cargoOwners); }
    public synchronized Map<Integer, Date> getInspectionDatesMap() { return new HashMap<>(this.inspectionDates); }
    public synchronized Map<Integer, Date> getInsertionDatesMap() { return new HashMap<>(this.insertionDates); }
}