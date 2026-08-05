package log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer die mehrsprachigen Logtexte.
 * Geprueft werden die vollstaendige Sprache, die prototypisch uebersetzte
 * zweite Sprache und der automatische Rueckgriff auf die Standardtexte.
 */
class LogTextsTest {

    @Test
    void deutscheSpracheLiefertDeutschenText() {
        LogTexts texte = new LogTexts("DE");
        assertEquals("Benutzerinteraktion: Kundin oder Kunde anlegen",
                texte.get(LogTexts.INSERT_CUSTOMER));
    }

    @Test
    void deutscheSpracheLiefertTextFuerZustandsaenderung() {
        LogTexts texte = new LogTexts("DE");
        assertEquals("Aenderung am Zustand der Geschaeftslogik",
                texte.get(LogTexts.STATE_CHANGED));
    }

    @Test
    void englischeSpracheLiefertUebersetztenText() {
        LogTexts texte = new LogTexts("EN");
        assertEquals("User interaction: add customer", texte.get(LogTexts.INSERT_CUSTOMER));
    }

    @Test
    void englischeSpracheLiefertUebersetzteZustandsaenderung() {
        LogTexts texte = new LogTexts("EN");
        assertEquals("Change of the business logic state", texte.get(LogTexts.STATE_CHANGED));
    }

    @Test
    void nichtUebersetzterSchluesselFaelltAufStandardtextZurueck() {
        // Die zweite Sprache ist prototypisch uebersetzt; fehlende Eintraege
        // werden automatisch aus der Standard-Datei uebernommen.
        LogTexts texte = new LogTexts("EN");
        assertEquals("Benutzerinteraktion: Frachtstueck entfernen",
                texte.get(LogTexts.DELETE_CARGO));
    }

    @Test
    void unbekannteSpracheVerwendetDieStandardtexte() {
        // Eine noch nicht hinterlegte Sprache fuehrt nicht zum Fehler.
        LogTexts texte = new LogTexts("FR");
        assertEquals("Benutzerinteraktion: Kundin oder Kunde anlegen",
                texte.get(LogTexts.INSERT_CUSTOMER));
    }

    @Test
    void unbekannterSchluesselLiefertDenSchluessel() {
        LogTexts texte = new LogTexts("DE");
        assertEquals("nicht.vorhanden", texte.get("nicht.vorhanden"));
    }
}
