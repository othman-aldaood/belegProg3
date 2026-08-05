package events;

/**
 * Beobachtermuster (Observer-Pattern) fuer Aenderungen am Zustand der
 * Geschaeftslogik. Wird bei jeder erfolgreichen Aenderung (Einfuegen,
 * Loeschen, Aktualisieren, Tauschen) benachrichtigt.
 * Der Beobachter erhaelt nur ein Signal ueber die Aenderung (keinen Inhalt)
 * und kann den aktuellen Zustand selbst bei der Geschaeftslogik abfragen.
 */
public interface ChangeObserver {

    /**
     * Wird bei jeder Aenderung am Zustand der Geschaeftslogik aufgerufen.
     */
    void onChanged();
}
