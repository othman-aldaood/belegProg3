package io;

import administration.Customer;
import cargo.Cargo;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Transferobjekt (JavaBean) fuer die JBP-Persistierung.
 * Der XMLEncoder benoetigt oeffentliche Getter/Setter und einen
 * Standardkonstruktor. Damit diese Beans-Konvention nicht die Kapselung der
 * Geschaeftslogik bricht, lebt dieses Objekt ausschliesslich in der
 * Persistenzschicht (DAL) und dient nur dem Transport des Zustands.
 */
public class WarehouseSnapshot {

    private int capacity;
    private int nextLocation = 1;
    private Set<Customer> customers = new HashSet<>();
    private Map<Integer, Cargo> cargos = new HashMap<>();
    private Map<Integer, Customer> cargoOwners = new HashMap<>();
    private Map<Integer, String> cargoTypes = new HashMap<>();
    private Map<Integer, Date> insertionDates = new HashMap<>();
    private Map<Integer, Date> inspectionDates = new HashMap<>();

    /**
     * Standardkonstruktor (zwingend erforderlich fuer JBP).
     */
    public WarehouseSnapshot() {
    }

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
        return this.customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers == null ? new HashSet<>() : customers;
    }

    public Map<Integer, Cargo> getCargos() {
        return this.cargos;
    }

    public void setCargos(Map<Integer, Cargo> cargos) {
        this.cargos = cargos == null ? new HashMap<>() : cargos;
    }

    public Map<Integer, Customer> getCargoOwners() {
        return this.cargoOwners;
    }

    public void setCargoOwners(Map<Integer, Customer> cargoOwners) {
        this.cargoOwners = cargoOwners == null ? new HashMap<>() : cargoOwners;
    }

    public Map<Integer, String> getCargoTypes() {
        return this.cargoTypes;
    }

    public void setCargoTypes(Map<Integer, String> cargoTypes) {
        this.cargoTypes = cargoTypes == null ? new HashMap<>() : cargoTypes;
    }

    public Map<Integer, Date> getInsertionDates() {
        return this.insertionDates;
    }

    public void setInsertionDates(Map<Integer, Date> insertionDates) {
        this.insertionDates = insertionDates == null ? new HashMap<>() : insertionDates;
    }

    public Map<Integer, Date> getInspectionDates() {
        return this.inspectionDates;
    }

    public void setInspectionDates(Map<Integer, Date> inspectionDates) {
        this.inspectionDates = inspectionDates == null ? new HashMap<>() : inspectionDates;
    }
}
