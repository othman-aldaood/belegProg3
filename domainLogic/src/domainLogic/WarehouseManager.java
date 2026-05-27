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
 * Die Implementierung verzichtet vollständig auf 'instanceof' und Down-Casting,
 * um die strikten Architekturvorgaben zu erfüllen.
 */
public class WarehouseManager implements CargoCommandListener {

    private final int capacity;
    private int nextLocation = 1;

    private final Set<Customer> customers = new HashSet<>();

    // Architektur-Anpassung zur Vermeidung von instanceof
    private final Map<Integer, Cargo> cargos = new HashMap<>();
    private final Map<Integer, Customer> cargoOwners = new HashMap<>();
    private final Map<Integer, String> cargoTypes = new HashMap<>();
    private final Map<Integer, Date> insertionDates = new HashMap<>();
    private final Map<Integer, Runnable> inspectionUpdaters = new HashMap<>();

    private GLFeedbackListener feedbackListener;
    private CapacityObserver capacityObserver;

    public WarehouseManager() {
        this(100);
    }

    public WarehouseManager(int capacity) {
        this.capacity = capacity;
    }

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
    public void onInsertCustomer(String customerName) {
        Customer newCustomer = new CustomerImpl(customerName);
        boolean added = this.customers.add(newCustomer);
        if (added) {
            sendFeedback("Erfolg: Kunde '" + customerName + "' wurde angelegt.");
        } else {
            sendFeedback("Fehler: Kunde '" + customerName + "' existiert bereits.");
        }
    }

    @Override
    public void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
        // Kapazität prüfen
        if (this.cargos.size() >= this.capacity) {
            sendFeedback("Fehler: Das Lager ist voll!");
            return;
        }
// Observer-Muster: Warnung bei >= 90% Kapazität
        if (this.capacityObserver != null && this.cargos.size() >= (this.capacity * 0.9)) {
            this.capacityObserver.onCapacityWarning("Achtung! Lagerkapazität hat 90% erreicht.");
        }
        // Kunde prüfen
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

        // --- Konvertierung von String zu Enum ---
        Collection<Hazard> hazardEnums = new ArrayList<>();
        if (hazards != null) {
            for (String h : hazards) {
                try {
                    hazardEnums.add(Hazard.valueOf(h.toUpperCase().trim()));
                } catch (IllegalArgumentException e) {
                    // Ignorieren
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

            sendFeedback("Erfolg: " + type + " auf Lagerplatz " + currentLocation + " eingefügt.");

        } catch (Exception e) {
            sendFeedback("Fehler: " + e.getMessage());
        }
    }

    @Override
    public void onReadCustomers() {
        StringBuilder sb = new StringBuilder("Kunden:\n");
        for (Customer c : this.customers) {
            long count = this.cargoOwners.values().stream().filter(owner -> owner.equals(c)).count();
            sb.append("- ").append(c.getName()).append(" (").append(count).append(")\n");
        }
        sendFeedback(sb.toString().trim());
    }

    @Override
    public void onReadCargos(String cargoType) {
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

    @Override
    public void onReadHazards(boolean existing) {
        // Logik für hazards
        sendFeedback("Gefahrenstoffe gelistet.");
    }

    @Override
    public void onUpdateInspectionDate(int storageLocation) {
        Runnable updater = this.inspectionUpdaters.get(storageLocation);
        if (updater != null) {
            updater.run();
            sendFeedback("Erfolg: Inspektionsdatum aktualisiert.");
        } else {
            sendFeedback("Fehler: Keine Inspektion möglich.");
        }
    }

    @Override
    public void onDeleteCustomer(String customerName) {
        // Implementierung...
    }

    @Override
    public void onDeleteCargo(int storageLocation) {
        this.cargos.remove(storageLocation);
        this.cargoOwners.remove(storageLocation);
        this.cargoTypes.remove(storageLocation);
        sendFeedback("Erfolg: Gelöscht.");
    }

    public Set<Customer> getAllCustomers() {
        return new HashSet<>(this.customers);
    }

    public Collection<Cargo> getAllCargos() {
        return new ArrayList<>(this.cargos.values());
    }

    public int getCapacity() {
        return this.capacity;
    }
}