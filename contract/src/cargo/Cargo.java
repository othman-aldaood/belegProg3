package cargo;

import administration.Storable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * Repräsentiert ein allgemeines Frachtstück im System.
 * Erweitert {@link Storable} und {@link Serializable} für JOS.
 */
public interface Cargo extends Storable, Serializable {

    /**
     * Gibt den Besitzer des Frachtstücks zurück.
     *
     * @return der Besitzer
     */
    administration.Customer getOwner();

    /**
     * Gibt den finanziellen Wert des Frachtstücks zurück.
     *
     * @return der Wert
     */
    BigDecimal getValue();

    /**
     * Gibt eine Sammlung der zugeordneten Gefahrenstoffe zurück.
     *
     * @return die Gefahrenstoffe
     */
    Collection<Hazard> getHazards();

    /**
     * Gibt das Datum der letzten Inspektion zurück.
     *
     * @return das Inspektionsdatum
     */
    Date getLastInspectionDate();

    /**
     * Setzt das Datum der letzten Inspektion.
     * Ersetzt die nicht-serialisierbare Runnable-Logik und erfüllt die JBP-Konvention.
     *
     * @param date das neue Inspektionsdatum
     */
    void setLastInspectionDate(Date date);
}