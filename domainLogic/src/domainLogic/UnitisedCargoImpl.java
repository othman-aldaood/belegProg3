package domainLogic;

import administration.Customer;
import cargo.UnitisedCargo;
import cargo.Hazard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Implementierung für Stückgut (Unitised Cargo).
 * Erfüllt die JavaBeans-Konvention für JBP.
 */
public class UnitisedCargoImpl implements UnitisedCargo, Serializable {

    private static final long serialVersionUID = 1L;

    private Customer owner;
    private int storageLocation;
    private Date insertionDate;
    private BigDecimal value;
    private Collection<Hazard> hazards;
    private boolean fragile;
    private Date lastInspectionDate;

    public UnitisedCargoImpl() {
        this.hazards = new HashSet<>();
        this.insertionDate = new Date();
    }

    public UnitisedCargoImpl(Customer owner, BigDecimal value, Collection<Hazard> hazards, boolean fragile) {
        this();
        this.owner = owner;
        this.value = value;
        this.fragile = fragile;
        if (hazards != null) {
            this.hazards.addAll(hazards);
        }
    }

    @Override
    public boolean isFragile() { return this.fragile; }

    // ohne @Override (da nicht im Interface)
    public void setFragile(boolean fragile) { this.fragile = fragile; }

    @Override
    public Customer getOwner() { return this.owner; }

    // ohne @Override
    public void setOwner(Customer owner) { this.owner = owner; }

    public Date getInsertionDate() { return this.insertionDate; }

    public void setInsertionDate(Date insertionDate) { this.insertionDate = insertionDate; }

    @Override
    public java.time.Duration getDurationOfStorage() {
        if (this.insertionDate == null) return java.time.Duration.ZERO;
        long diff = new Date().getTime() - this.insertionDate.getTime();
        return java.time.Duration.ofMillis(diff);
    }

    @Override
    public Date getLastInspectionDate() { return this.lastInspectionDate; }

    // ohne @Override
    public void setLastInspectionDate(Date lastInspectionDate) { this.lastInspectionDate = lastInspectionDate; }

    @Override
    public int getStorageLocation() { return this.storageLocation; }

    // ohne @Override
    public void setStorageLocation(int location) { this.storageLocation = location; }

    @Override
    public BigDecimal getValue() { return this.value; }

    public void setValue(BigDecimal value) { this.value = value; }

    @Override
    public Collection<Hazard> getHazards() {
        return new HashSet<>(this.hazards);
    }

    public void setHazards(Collection<Hazard> hazards) {
        if (hazards != null) {
            this.hazards = new HashSet<>(hazards);
        } else {
            this.hazards = new HashSet<>();
        }
    }

    @Override
    public UnitisedCargoImpl copy(int storageLocation) {
        // Tiefe Kopie (Prototype): auch Eigentuemerin und Daten werden kopiert,
        // damit Aenderungen an der Kopie die Geschaeftslogik nicht erreichen.
        Customer ownerCopy = this.owner == null ? null : new CustomerImpl(this.owner.getName());
        UnitisedCargoImpl copy = new UnitisedCargoImpl(ownerCopy, this.value, this.hazards, this.fragile);
        copy.setStorageLocation(storageLocation);
        copy.setInsertionDate(this.insertionDate == null ? null : new Date(this.insertionDate.getTime()));
        copy.setLastInspectionDate(this.lastInspectionDate == null ? null : new Date(this.lastInspectionDate.getTime()));
        return copy;
    }
}