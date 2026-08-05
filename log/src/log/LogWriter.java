package log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Schreibt die Logeintraege zeilenweise in eine Ausgabe.
 * Das Log ist eine singulaere Ressource: alle Schreibzugriffe sind
 * synchronisiert, dadurch ist der Zugriff auch bei nebenlaeufiger Nutzung
 * (mehrere Threads) geschuetzt und die Eintraege bleiben vollstaendig.
 * Die Ausgabe wird im Konstruktor uebergeben, damit die Klasse unabhaengig
 * vom Dateisystem verwendbar und testbar ist.
 */
public class LogWriter {

    /** Dateiname des Logs laut Anforderung (ohne Pfad, im Ausfuehrungsverzeichnis). */
    public static final String LOG_FILENAME = "log.txt";

    private final Writer writer;
    private final SimpleDateFormat zeitstempelFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Erzeugt einen LogWriter fuer die uebergebene Ausgabe.
     *
     * @param writer die Ausgabe, in die die Eintraege geschrieben werden
     */
    public LogWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Erzeugt einen LogWriter, der an die Logdatei im Ausfuehrungsverzeichnis
     * anhaengt.
     *
     * @return der LogWriter fuer die Logdatei
     * @throws IOException falls die Datei nicht geoeffnet werden kann
     */
    public static LogWriter forLogFile() throws IOException {
        return new LogWriter(new FileWriter(LOG_FILENAME, true));
    }

    /**
     * Schreibt einen Logeintrag als eine Zeile mit vorangestelltem Zeitstempel.
     * Synchronisiert, da das Log eine singulaere Ressource ist.
     *
     * @param eintrag der zu protokollierende Text
     */
    public synchronized void write(String eintrag) {
        try {
            this.writer.write(this.zeitstempelFormat.format(new Date()) + " | " + eintrag);
            this.writer.write(System.lineSeparator());
            this.writer.flush();
        } catch (IOException e) {
            // Ein fehlgeschlagener Logeintrag darf die Anwendung nicht beenden.
            System.out.println("Fehler beim Schreiben des Logs: " + e.getMessage());
        }
    }
}
