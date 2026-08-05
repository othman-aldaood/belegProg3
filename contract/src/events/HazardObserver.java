package events;

/**
 * Beobachtermuster (Observer-Pattern) zur Ueberwachung der im Lager
 * vorhandenen Gefahrenstoffe.
 * Der Beobachter erhaelt nur ein Signal ueber die Aenderung (keinen Inhalt)
 * und kann den aktuellen Zustand selbst bei der Geschaeftslogik abfragen.
 */
public interface HazardObserver {

    /**
     * Wird aufgerufen, wenn sich die vorhandenen Gefahrenstoffe im Lager
     * geaendert haben.
     */
    void onHazardsChanged();
}
