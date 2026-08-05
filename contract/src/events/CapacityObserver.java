package events;

/**
 * Beobachtermuster (Observer-Pattern) zur Überwachung der Lagerkapazität.
 * Der Beobachter erhält nur ein Signal über die Änderung (keinen Inhalt)
 * und kann den aktuellen Zustand selbst bei der Geschäftslogik abfragen.
 */
public interface CapacityObserver {

    /**
     * Wird aufgerufen, wenn die Kapazität des Lagers einen kritischen Wert (z.B. 90%) erreicht.
     */
    void onCapacityWarning();
}
