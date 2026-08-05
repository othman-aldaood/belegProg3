package log;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Liefert die Texte der Logeintraege in der gewaehlten Sprache.
 * Die Texte stehen nicht im Quellcode, sondern in Properties-Dateien
 * (LogTexts.properties als Standard, LogTexts_&lt;Sprachkuerzel&gt;.properties je
 * Sprache). Eine weitere Sprache wird durch Hinzufuegen einer entsprechenden
 * Datei unterstuetzt, ohne dass der Quellcode geaendert werden muss.
 * Fehlt eine Uebersetzung, wird automatisch der Text der Standard-Datei
 * verwendet; dadurch kann eine Sprache auch nur teilweise uebersetzt sein.
 */
public class LogTexts {

    private static final String BASE_NAME = "log.LogTexts";

    // Schluessel der Logeintraege
    public static final String INSERT_CUSTOMER = "interaktion.kundin.anlegen";
    public static final String INSERT_CARGO = "interaktion.fracht.einfuegen";
    public static final String READ_CUSTOMERS = "interaktion.kundinnen.anzeigen";
    public static final String READ_CARGOS = "interaktion.fracht.anzeigen";
    public static final String READ_HAZARDS = "interaktion.gefahrenstoffe.anzeigen";
    public static final String UPDATE_INSPECTION = "interaktion.inspektion.setzen";
    public static final String DELETE_CUSTOMER = "interaktion.kundin.loeschen";
    public static final String DELETE_CARGO = "interaktion.fracht.loeschen";
    public static final String STATE_CHANGED = "aenderung.zustand";

    private final ResourceBundle texte;

    /**
     * Erzeugt die Textquelle fuer das uebergebene Sprachkuerzel (z.B. "DE" oder "EN").
     *
     * @param sprachkuerzel das Kuerzel der gewuenschten Sprache
     */
    public LogTexts(String sprachkuerzel) {
        // Ohne Rueckgriff auf die Sprache des Betriebssystems: es gilt entweder
        // die angeforderte Sprache oder die Standard-Datei. Damit ist das
        // Verhalten auf jedem Rechner gleich.
        this.texte = ResourceBundle.getBundle(BASE_NAME,
                new Locale(sprachkuerzel.toLowerCase()),
                ResourceBundle.Control.getNoFallbackControl(
                        ResourceBundle.Control.FORMAT_PROPERTIES));
    }

    /**
     * Liefert den Text zum uebergebenen Schluessel in der gewaehlten Sprache.
     *
     * @param schluessel der Schluessel des Logeintrags
     * @return der uebersetzte Text, ersatzweise der Schluessel selbst
     */
    public String get(String schluessel) {
        try {
            return this.texte.getString(schluessel);
        } catch (MissingResourceException e) {
            // Ohne hinterlegten Text wird der Schluessel protokolliert,
            // damit der Eintrag nachvollziehbar bleibt.
            return schluessel;
        }
    }
}
