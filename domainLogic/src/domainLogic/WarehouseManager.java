package domainLogic;

import administration.Customer;
import cargo.Cargo;
import cargo.Hazard;

import java.util.*;

/**
 * Diese Klasse verwaltet das Warenlager, die Kunden und die Frachtstücke.
 */
public class WarehouseManager {
    private final int capacity; // Maximale Kapazität des Lagers
    private final Set<Customer> customers; // Menge der registrierten Kunden
    private final Map<Integer, Cargo> cargos; // Speichert Frachtstücke nach ihrer Position
    private int nextLocation; // Nächste freie Lagerposition

    public WarehouseManager(int capacity) {
        this.capacity = capacity;
        this.customers = new HashSet<>();
        this.cargos = new HashMap<>();
        this.nextLocation = 1;
    }

    // Registriert einen neuen Kunden im System
    public boolean addCustomer(Customer c) {
        return this.customers.add(c);
    }

    // Entfernt einen bestehenden Kunden
    public boolean removeCustomer(Customer customer) {
        return this.customers.remove(customer);
    }

    // Gibt alle registrierten Kunden zurück
    public Set<Customer> getAllCustomers() {
        return new HashSet<>(this.customers);
    }

    /**
     * Hilfsmethode, um den Besitzer eines Frachtstücks zu ermitteln.
     * Dies ist notwendig, da die Schnittstelle 'Cargo' keine getOwner-Methode hat.
     */
    private Customer getOwnerOfCargo(Cargo cargo) {
        if (cargo instanceof DryBulkCargoImpl) {
            return ((DryBulkCargoImpl) cargo).getOwner();
        } else if (cargo instanceof UnitisedCargoImpl) {
            return ((UnitisedCargoImpl) cargo).getOwner();
        }
        return null;
    }

    // Lagert ein neues Frachtstück ein, wenn Kapazität und Kunde gültig sind
    public boolean insertCargo(Cargo cargo) {
        // Prüfung der Lagerkapazität
        if (this.cargos.size() >= this.capacity) {
            return false;
        }

        // Ermittlung des Besitzers über die Hilfsmethode
        Customer owner = getOwnerOfCargo(cargo);

        // Prüfung, ob der Besitzer im System registriert ist
        if (owner == null || !this.customers.contains(owner)) {
            return false;
        }

        // Zuweisung der Lagerposition basierend auf dem aktuellen Zähler
        if (cargo instanceof DryBulkCargoImpl) {
            ((DryBulkCargoImpl) cargo).setStorageLocation(this.nextLocation);
        } else if (cargo instanceof UnitisedCargoImpl) {
            ((UnitisedCargoImpl) cargo).setStorageLocation(this.nextLocation);
        }

        this.cargos.put(this.nextLocation, cargo);
        this.nextLocation++;
        return true;
    }

    // Gibt alle aktuell eingelagerten Frachtstücke zurück
    public Collection<Cargo> getAllCargos() {
        return this.cargos.values();
    }

    // Aktualisiert das Inspektionsdatum für Schüttgut
    public boolean updateInspectionDate(int location, Date newDate) {
        Cargo cargo = this.cargos.get(location);
        if (cargo instanceof DryBulkCargoImpl) {
            ((DryBulkCargoImpl) cargo).setLastInspectionDate(newDate);
            return true;
        }
        return false;
    }

    // Entfernt ein Frachtstück von einem bestimmten Lagerplatz
    public boolean removeCargo(int location) {
        if (this.cargos.containsKey(location)) {
            this.cargos.remove(location);
            return true;
        }
        return false;
    }

    // Gibt die Gesamtkapazität des Lagers zurück
    public int getCapacity() {
        return this.capacity;
    }

    // Ermittelt alle Gefahrenstoffe, die sich momentan im Lager befinden
    public Set<Hazard> getPresentHazards() {
        Set<Hazard> presentHazards = new HashSet<>();
        for (Cargo cargo : cargos.values()) {
            if (cargo.getHazards() != null) {
                presentHazards.addAll(cargo.getHazards());
            }
        }
        return presentHazards;
    }

    // Quelle: LLM Gemini unterstüzung
    // Statistik: Wie viele Frachtstücke besitzt jeder Kunde?
    public Map<Customer, Integer> getCustomersWithCargoCount() {
        Map<Customer, Integer> countMap = new HashMap<>();
        for (Customer c : customers) {
            int count = 0;
            for (Cargo cargo : cargos.values()) {
                Customer owner = getOwnerOfCargo(cargo);
                if (c.equals(owner)) {
                    count++;
                }
            }
            countMap.put(c, count);
        }
        return countMap;
    }
}