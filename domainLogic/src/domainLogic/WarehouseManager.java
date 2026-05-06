package domainLogic;

import administration.Customer;
import cargo.DryBulkCargo;

import java.util.*;

public class WarehouseManager {
    private int capacity;
    private Set<Customer> customers;
    private Map<Integer, DryBulkCargo> cargos;
    private int nextLocation;

    public WarehouseManager(int capacity) {
        this.capacity = capacity;
        this.customers = new HashSet<>();
        this.cargos = new HashMap<>();
        this.nextLocation = 1;
    }

    public boolean addCustomer(Customer c) {
        return this.customers.add(c);
    }

    public boolean insertCargo(DryBulkCargo cargo) {
        if (this.cargos.size() >= this.capacity) {
            return false;
        }
        if (!this.customers.contains(cargo.getOwner())) {
            return false;
        }

        if (cargo instanceof DryBulkCargoImpl) {
            ((DryBulkCargoImpl) cargo).setStorageLocation(this.nextLocation);
        }

        this.cargos.put(this.nextLocation, cargo);
        this.nextLocation++;
        return true;
    }

    public Collection<DryBulkCargo> getAllCargos() {
        return this.cargos.values();
    }

    public boolean updateInspectionDate(int location, Date newDate) {
        DryBulkCargo cargo = this.cargos.get(location);
        if (cargo != null) {
            if (cargo instanceof DryBulkCargoImpl) {
                ((DryBulkCargoImpl) cargo).setLastInspectionDate(newDate);
            }
            return true;
        }
        return false;
    }

    public boolean removeCargo(int location) {
        if (this.cargos.containsKey(location)) {
            this.cargos.remove(location);
            return true;
        }
        return false;
    }

}
