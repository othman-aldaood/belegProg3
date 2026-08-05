package log;

import events.CargoCommandListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests fuer den protokollierenden Stellvertreter.
 * Geprueft wird sowohl die Weiterleitung an die Geschaeftslogik (per Mockito)
 * als auch der geschriebene Logeintrag; die Ausgabe erfolgt in den Speicher,
 * daher ohne Dateisystemzugriff.
 */
class LoggingCommandListenerTest {

    @Test
    void insertCustomerWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onInsertCustomer("Alice");

        Mockito.verify(glMock).onInsertCustomer("Alice");
    }

    @Test
    void insertCustomerWirdProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingCommandListener listener = new LoggingCommandListener(
                Mockito.mock(CargoCommandListener.class), new LogWriter(ausgabe), new LogTexts("DE"));

        listener.onInsertCustomer("Alice");

        assertTrue(ausgabe.toString().contains("Kundin oder Kunde anlegen"));
    }

    @Test
    void insertCargoWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.TEN,
                Collections.emptyList(), false, false, 5);

        Mockito.verify(glMock).onInsertCargo("DryBulkCargo", "Alice", BigDecimal.TEN,
                Collections.emptyList(), false, false, 5);
    }

    @Test
    void insertCargoWirdProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingCommandListener listener = new LoggingCommandListener(
                Mockito.mock(CargoCommandListener.class), new LogWriter(ausgabe), new LogTexts("DE"));

        listener.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.TEN,
                Collections.emptyList(), false, false, 5);

        assertTrue(ausgabe.toString().contains("Frachtstueck einfuegen"));
    }

    @Test
    void readCustomersWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onReadCustomers();

        Mockito.verify(glMock).onReadCustomers();
    }

    @Test
    void readCargosWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onReadCargos("DryBulkCargo");

        Mockito.verify(glMock).onReadCargos("DryBulkCargo");
    }

    @Test
    void readHazardsWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onReadHazards(true);

        Mockito.verify(glMock).onReadHazards(true);
    }

    @Test
    void updateInspectionDateWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onUpdateInspectionDate(1);

        Mockito.verify(glMock).onUpdateInspectionDate(1);
    }

    @Test
    void updateInspectionDateWirdProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingCommandListener listener = new LoggingCommandListener(
                Mockito.mock(CargoCommandListener.class), new LogWriter(ausgabe), new LogTexts("DE"));

        listener.onUpdateInspectionDate(1);

        assertTrue(ausgabe.toString().contains("Inspektionsdatum setzen"));
    }

    @Test
    void deleteCustomerWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onDeleteCustomer("Alice");

        Mockito.verify(glMock).onDeleteCustomer("Alice");
    }

    @Test
    void deleteCargoWirdAnDieGeschaeftslogikWeitergeleitet() {
        CargoCommandListener glMock = Mockito.mock(CargoCommandListener.class);
        LoggingCommandListener listener = new LoggingCommandListener(glMock,
                new LogWriter(new StringWriter()), new LogTexts("DE"));

        listener.onDeleteCargo(1);

        Mockito.verify(glMock).onDeleteCargo(1);
    }

    @Test
    void deleteCargoWirdProtokolliert() {
        StringWriter ausgabe = new StringWriter();
        LoggingCommandListener listener = new LoggingCommandListener(
                Mockito.mock(CargoCommandListener.class), new LogWriter(ausgabe), new LogTexts("DE"));

        listener.onDeleteCargo(1);

        assertTrue(ausgabe.toString().contains("Frachtstueck entfernen"));
    }

    @Test
    void protokollWirdInDerGewaehltenSpracheGeschrieben() {
        StringWriter ausgabe = new StringWriter();
        LoggingCommandListener listener = new LoggingCommandListener(
                Mockito.mock(CargoCommandListener.class), new LogWriter(ausgabe), new LogTexts("EN"));

        listener.onInsertCustomer("Alice");

        assertTrue(ausgabe.toString().contains("User interaction: add customer"));
    }
}
