package log;

import events.ChangeObserver;

/**
 * Protokolliert jede Aenderung am Zustand der Geschaeftslogik.
 * Der Beobachter wird im setup an der Geschaeftslogik registriert; die
 * Eintraege sind laut Anforderung unspezifisch gehalten.
 */
public class LoggingChangeObserver implements ChangeObserver {

    private final LogWriter writer;
    private final LogTexts texte;

    /**
     * Erzeugt den protokollierenden Beobachter.
     *
     * @param writer die Ausgabe der Logeintraege
     * @param texte  die Textquelle der gewaehlten Sprache
     */
    public LoggingChangeObserver(LogWriter writer, LogTexts texte) {
        this.writer = writer;
        this.texte = texte;
    }

    @Override
    public void onChanged() {
        this.writer.write(this.texte.get(LogTexts.STATE_CHANGED));
    }
}
