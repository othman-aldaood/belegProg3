package net;

import events.CargoCommandListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Stellvertreter-Tests fuer den CommandProcessor (Prototyp 6, Netz).
 *
 * Der CommandProcessor ist die transportunabhaengige Verarbeitungsstelle:
 * sowohl der TCP- als auch der UDP-Server leiten die empfangene Befehlszeile
 * an genau diesen Processor weiter. Deshalb wird hier - stellvertretend fuer
 * beide Server - geprueft, dass eine Protokoll-Zeile das korrekte Event an die
 * Geschaeftslogik (CargoCommandListener) ausloest. Die GL wird per Mockito
 * ersetzt; die Tests sind dadurch ohne Netzwerk schnell und deterministisch.
 *
 * Testvorgaben: genau ein verify pro Test, keine Schleifen, kein @BeforeEach.
 *
 */
class CommandProcessorTest {

    // --- TCP-Server: der TCPServer ruft processor.process(zeile) unveraendert auf. ---

    /**
     * Einfuegen (TCP): die Zeile "INSERT_CUSTOMER;Alice" muss das Event
     * onInsertCustomer("Alice") an der Geschaeftslogik ausloesen.
     */
    @Test
    void testTcpInsertCommandTriggersOnInsertCustomer() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        CommandProcessor processor = new CommandProcessor(mockListener);

        processor.process("INSERT_CUSTOMER;Alice");

        Mockito.verify(mockListener).onInsertCustomer("Alice");
    }

    /**
     * Lesen (TCP): die Zeile "READ_CUSTOMERS" muss das Event
     * onReadCustomers() an der Geschaeftslogik ausloesen.
     */
    @Test
    void testTcpReadCommandTriggersOnReadCustomers() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        CommandProcessor processor = new CommandProcessor(mockListener);

        processor.process("READ_CUSTOMERS");

        Mockito.verify(mockListener).onReadCustomers();
    }

    // --- UDP-Server: der UDPServer entfernt zuerst die Request-ID ("id;Befehl")
    //     und uebergibt den reinen Befehl an DENSELBEN CommandProcessor. Getestet
    //     wird daher erneut der Processor mit der bereits bereinigten Befehlszeile. ---

    /**
     * Einfuegen (UDP): nach dem Entfernen der Request-ID durch den UDPServer
     * erreicht "INSERT_CUSTOMER;Bob" den Processor und muss
     * onInsertCustomer("Bob") ausloesen.
     */
    @Test
    void testUdpInsertCommandTriggersOnInsertCustomer() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        CommandProcessor processor = new CommandProcessor(mockListener);

        processor.process("INSERT_CUSTOMER;Bob");

        Mockito.verify(mockListener).onInsertCustomer("Bob");
    }

    /**
     * Lesen (UDP): nach dem Entfernen der Request-ID erreicht "READ_CARGOS;UnitisedCargo"
     * den Processor und muss onReadCargos("UnitisedCargo") ausloesen.
     */
    @Test
    void testUdpReadCommandTriggersOnReadCargos() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        CommandProcessor processor = new CommandProcessor(mockListener);

        processor.process("READ_CARGOS;UnitisedCargo");

        Mockito.verify(mockListener).onReadCargos("UnitisedCargo");
    }
}
