package log;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests fuer den protokollierenden Beobachter der Zustandsaenderungen.
 * Die Ausgabe erfolgt in den Speicher, daher ohne Dateisystemzugriff.
 */
class LoggingChangeObserverTest {

    @Test
    void aenderungWirdProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingChangeObserver observer = new LoggingChangeObserver(
                new LogWriter(ausgabe), new LogTexts("DE"));

        observer.onChanged();

        assertTrue(ausgabe.toString().contains("Aenderung am Zustand der Geschaeftslogik"));
    }

    @Test
    void aenderungWirdInDerGewaehltenSpracheProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingChangeObserver observer = new LoggingChangeObserver(
                new LogWriter(ausgabe), new LogTexts("EN"));

        observer.onChanged();

        assertTrue(ausgabe.toString().contains("Change of the business logic state"));
    }

    @Test
    void ohneAenderungWirdNichtsProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        new LoggingChangeObserver(new LogWriter(ausgabe), new LogTexts("DE"));

        assertTrue(ausgabe.toString().isEmpty());
    }
}
