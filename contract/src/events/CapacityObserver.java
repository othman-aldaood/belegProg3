package events;

/**
 * Beobachtermuster (Observer-Pattern) zur Überwachung der Lagerkapazität.
 */
public interface CapacityObserver {

    /**
     * Wird aufgerufen, wenn die Kapazität des Lagers einen kritischen Wert (z.B. 90%) erreicht.
     * @param message Die Warnmeldung, die ausgegeben werden soll.
     */
    void onCapacityWarning(String message);
}