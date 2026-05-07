package domainLogic;

import administration.Customer;
import cargo.DryBulkCargo;
import cargo.Hazard;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

// Implementierung für Schüttgut (Dry Bulk Cargo)
public class DryBulkCargoImpl implements DryBulkCargo {

    private final Customer owner;
    private int storageLocation;
    private Date lastInspectionDate;
    private final Date insertionDate;
    private final BigDecimal value;
    private final Collection<Hazard> hazards;
    private final int grainSize;

    // Konstruktor für ein neues Schüttgut-Objekt
    public DryBulkCargoImpl(Customer owner, BigDecimal value, Collection<Hazard> hazards, int grainSize) {
        this.owner = owner;
        this.value = value;
        this.grainSize = grainSize;
        // Sicherstellen, dass die Liste der Gefahrenstoffe nicht null ist
        if (hazards != null) {
            this.hazards = new HashSet<>(hazards);
        } else {
            this.hazards = new HashSet<>();
        }
        this.insertionDate = new Date(); // Speichert den Zeitpunkt der Einlagerung
    }

    @Override
    public Customer getOwner() {
        return owner;
    }

    // Berechnet die aktuelle Lagerdauer
    @Override
    public java.time.Duration getDurationOfStorage() {
        if (insertionDate == null) return null;
        long currentTime = new Date().getTime();
        long startTime = insertionDate.getTime();
        return java.time.Duration.ofMillis(currentTime - startTime);
    }

    @Override
    public Date getLastInspectionDate() {
        return lastInspectionDate;
    }

    public void setLastInspectionDate(Date lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    @Override
    public int getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(int storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Collection<Hazard> getHazards() {
        return hazards;
    }

    @Override
    public int getGrainSize() {
        return grainSize;
    }
}