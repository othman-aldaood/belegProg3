package domainLogic;

import administration.Customer;
import cargo.UnitisedCargo;
import cargo.Hazard;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Implementierung für Stückgut (Unitised Cargo).
 * Erfüllt die Anforderungen des Contract-Interfaces und erweitert
 * die Funktionalität um Inspektionsdaten, um Kompilierungsfehler zu beheben.
 */
public class UnitisedCargoImpl implements UnitisedCargo {

    private final Customer owner;
    private int storageLocation;
    private final Date insertionDate;
    private final BigDecimal value;
    private final Collection<Hazard> hazards;
    private final boolean fragile;
    private Date lastInspectionDate; // Behebt den Kompilierungsfehler im WarehouseManager

    /**
     * Konstruktor für ein neues Stückgut-Objekt.
     * @param owner Der Besitzer des Frachtstücks.
     * @param value Der finanzielle Wert des Frachtstücks.
     * @param hazards Die Liste der zugeordneten Gefahrenstoffe.
     * @param fragile Gibt an, ob das Frachtstück zerbrechlich ist.
     */
    public UnitisedCargoImpl(Customer owner, BigDecimal value, Collection<Hazard> hazards, boolean fragile) {
        this.owner = owner;
        this.value = value;
        this.fragile = fragile;
        if (hazards != null) {
            this.hazards = new HashSet<>(hazards);
        } else {
            this.hazards = new HashSet<>();
        }
        this.insertionDate = new Date(); // Setzt das Einfügedatum bei der Erstellung
    }

    @Override
    public boolean isFragile() {
        return this.fragile;
    }

    @Override
    public Customer getOwner() {
        return this.owner;
    }

    /**
     * Berechnet die aktuelle Lagerdauer basierend auf dem Einfügedatum.
     * @return Die Dauer als java.time.Duration.
     */
    @Override
    public java.time.Duration getDurationOfStorage() {
        long diff = new Date().getTime() - this.insertionDate.getTime();
        return java.time.Duration.ofMillis(diff);
    }

    @Override
    public Date getLastInspectionDate() {
        return this.lastInspectionDate;
    }

    /**
     * Setzt das Datum der letzten Überprüfung.
     * @param lastInspectionDate Das neue Inspektionsdatum.
     */
    public void setLastInspectionDate(Date lastInspectionDate) {
        this.lastInspectionDate = lastInspectionDate;
    }

    @Override
    public int getStorageLocation() {
        return this.storageLocation;
    }

    @Override
    public void setStorageLocation(int location) {
        this.storageLocation = location;
    }

    @Override
    public BigDecimal getValue() {
        return this.value;
    }

    @Override
    public Collection<Hazard> getHazards() {
        return this.hazards;
    }
}