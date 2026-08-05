package domainLogic;

import administration.Customer;
import cargo.DryBulkCargo;
import cargo.Hazard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Implementierung für Schüttgut (Dry Bulk Cargo).
 * Diese Klasse wurde für die JOS- und JBP-Persistierung angepasst (JavaBeans-Konvention).
 */
public class DryBulkCargoImpl implements DryBulkCargo, Serializable {

    private static final long serialVersionUID = 1L;

    private Customer owner;
    private int storageLocation;
    private Date lastInspectionDate;
    private Date insertionDate;
    private BigDecimal value;
    private Collection<Hazard> hazards;
    private int grainSize;

    public DryBulkCargoImpl() {
        this.hazards = new HashSet<>();
        this.insertionDate = new Date();
    }

    public DryBulkCargoImpl(Customer owner, BigDecimal value, Collection<Hazard> hazards, int grainSize) {
        this();
        this.owner = owner;
        this.value = value;
        this.grainSize = grainSize;
        if (hazards != null) {
            this.hazards.addAll(hazards);
        }
    }

    @Override
    public Customer getOwner() {
        return this.owner;
    }

    // ohne @Override
    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    public Date getInsertionDate() {
        return this.insertionDate;
    }

    public void setInsertionDate(Date insertionDate) {
        this.insertionDate = insertionDate;
    }

    @Override
    public java.time.Duration getDurationOfStorage() {
        if (this.insertionDate == null) return null;
        long currentTime = new Date().getTime();
        long startTime = this.insertionDate.getTime();
        return java.time.Duration.ofMillis(currentTime - startTime);
    }

    @Override
    public Date getLastInspectionDate() {
        return this.lastInspectionDate;
    }

    // ohne @Override
    public void setLastInspectionDate(Date lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    @Override
    public int getStorageLocation() {
        return this.storageLocation;
    }

    // ohne @Override
    public void setStorageLocation(int storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public BigDecimal getValue() {
        return this.value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

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
    public int getGrainSize() {
        return this.grainSize;
    }

    public void setGrainSize(int grainSize) {
        this.grainSize = grainSize;
    }

    @Override
    public DryBulkCargoImpl copy(int storageLocation) {
        // Tiefe Kopie (Prototype): auch Eigentuemerin und Daten werden kopiert,
        // damit Aenderungen an der Kopie die Geschaeftslogik nicht erreichen.
        Customer ownerCopy = this.owner == null ? null : new CustomerImpl(this.owner.getName());
        DryBulkCargoImpl copy = new DryBulkCargoImpl(ownerCopy, this.value, this.hazards, this.grainSize);
        copy.setStorageLocation(storageLocation);
        copy.setInsertionDate(this.insertionDate == null ? null : new Date(this.insertionDate.getTime()));
        copy.setLastInspectionDate(this.lastInspectionDate == null ? null : new Date(this.lastInspectionDate.getTime()));
        return copy;
    }
}