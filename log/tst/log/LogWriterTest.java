package log;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer den LogWriter.
 * Es wird in eine Ausgabe im Speicher geschrieben, damit die Tests ohne
 * Dateisystemzugriff auskommen und ueberall schnell ausfuehrbar sind.
 */
class LogWriterTest {

    @Test
    void writeSchreibtDenEintrag() {
        StringWriter ausgabe = new StringWriter();
        LogWriter writer = new LogWriter(ausgabe);

        writer.write("Testeintrag");

        assertTrue(ausgabe.toString().contains("Testeintrag"));
    }

    @Test
    void writeBeendetDenEintragMitZeilenwechsel() {
        StringWriter ausgabe = new StringWriter();
        LogWriter writer = new LogWriter(ausgabe);

        writer.write("Erster Eintrag");

        assertTrue(ausgabe.toString().endsWith(System.lineSeparator()));
    }

    @Test
    void writeStelltDemEintragEinenZeitstempelVoran() {
        StringWriter ausgabe = new StringWriter();
        LogWriter writer = new LogWriter(ausgabe);

        writer.write("Testeintrag");

        assertTrue(ausgabe.toString().startsWith("2"));
    }

    @Test
    void writeTrenntZeitstempelUndEintrag() {
        StringWriter ausgabe = new StringWriter();
        LogWriter writer = new LogWriter(ausgabe);

        writer.write("Testeintrag");

        assertTrue(ausgabe.toString().contains(" | Testeintrag"));
    }

    @Test
    void logDateiHeisstLogTxt() {
        assertEquals("log.txt", LogWriter.LOG_FILENAME);
    }
}
