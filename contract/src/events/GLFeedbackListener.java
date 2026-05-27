package events;

/**
 * Schnittstelle für die Kommunikation von der Geschäftslogik (GL) zur Benutzeroberfläche (CLI).
 * Realisiert das erweiterte Event-System für Rückmeldungen (z.B. bei Lese-Anfragen).
 */
public interface GLFeedbackListener {

    /**
     * Empfängt eine Rückmeldung aus der Geschäftslogik zur Darstellung in der UI.
     * @param feedback Die darzustellende Nachricht oder die Liste der Elemente als Text.
     */
    void onFeedbackReceived(String feedback);
}