package administration;

import java.time.Duration;
import java.util.Date;
import java.io.Serializable;

/**
 * Schnittstelle für Objekte, die im Lager aufbewahrt werden können (Storables).
 * Erweitert {@link Serializable}, um die Persistierung der Geschäftslogik
 * über Java Object Serialization (JOS) zu ermöglichen.
 */
public interface Storable extends Serializable {

    /**
     * Liefert den Besitzer des lagerbaren Objekts.
     *
     * @return der Besitzer (Customer) des Obachtsobjekts
     */
    Customer getOwner();

    /**
     * Liefert die vergangene Zeit seit dem Einfügen des Objekts in das Lager.
     *
     * @return die vergangene Zeit als {@link Duration} oder null, wenn kein Einfügedatum gesetzt ist
     */
    Duration getDurationOfStorage();

    /**
     * Liefert das Datum, an dem die letzte Inspektion des Objekts durchgeführt wurde.
     *
     * @return das Datum der letzten Inspektion
     */
    Date getLastInspectionDate();

    /**
     * Liefert den aktuellen Lagerplatz, an dem das Objekt im Lager verwaltet wird.
     *
     * @return die Nummer des Lagerplatzes
     */
    int getStorageLocation();
}