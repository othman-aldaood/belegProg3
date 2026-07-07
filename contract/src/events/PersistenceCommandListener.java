package events;

/**
 * Schnittstelle fuer Persistenz-Befehle von der Benutzeroberfläche.
 * Die UI kennt dabei weder die Geschäftslogik noch die Persistenz-Technologie,
 * die Verdrahtung erfolgt im setup (main-Methode).
 */
public interface PersistenceCommandListener {

    /**
     * Speichert den Zustand der Geschäftslogik.
     * @param technology "JOS" oder "JBP"
     */
    void onSave(String technology);

    /**
     * Lädt den Zustand der Geschäftslogik.
     * @param technology "JOS" oder "JBP"
     */
    void onLoad(String technology);
}
