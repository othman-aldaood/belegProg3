package domainLogic;

import administration.Customer;
import cargo.UnitisedCargo;
import cargo.Hazard;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

// Implementierung für Stückgut (Unitised Cargo)
public class UnitisedCargoImpl implements UnitisedCargo {

    private final Customer owner;
    private int storageLocation;
    private final Date insertionDate;
    private final BigDecimal value;
    private final Collection<Hazard> hazards;
    private final boolean fragile;

    // Konstruktor aktualisiert: wir haben 'fragile' hinzugefügt
    public UnitisedCargoImpl(Customer owner, BigDecimal value, Collection<Hazard> hazards, boolean fragile) {
        this.owner = owner;
        this.value = value;
        this.fragile = fragile;
        if (hazards != null) {
            this.hazards = new HashSet<>(hazards);
        } else {
            this.hazards = new HashSet<>();
        }
        this.insertionDate = new Date();
    }

    @Override
    public boolean isFragile() {
        return fragile;
    }

    @Override
    public Customer getOwner() {
        return owner;
    }

    @Override
    public java.time.Duration getDurationOfStorage() {
        long diff = new Date().getTime() - insertionDate.getTime();
        return java.time.Duration.ofMillis(diff);
    }

    @Override
    public Date getLastInspectionDate() {
        return null;
    }

    @Override
    public int getStorageLocation() {
        return storageLocation;
    }

    @Override
    public void setStorageLocation(int location) {
        this.storageLocation = location;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public Collection<Hazard> getHazards() {
        return hazards;
    }
}