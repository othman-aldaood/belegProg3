package cli;

import events.CargoCommandListener;
import events.PersistenceCommandListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testklasse für den ConsoleClient.
 * Überprüft das Event-System und die Ein-/Ausgabe streng nach den akademischen Vorgaben:
 * - Nur eine Zusicherung (Assert/Verify) pro Test
 * - Keine Schleifen
 * - Mocking der Geschäftslogik (keine Impl-Klassen)
 */
class ConsoleClientTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    // Kein @BeforeEach: nur der erste Test benötigt die Umleitung von System.out.
    // Laut Testvorgaben soll @BeforeEach nur verwendet werden, wenn JEDER Test
    // vom gesamten setup abhängt.

    @AfterEach
    void tearDown() {
        // Stellt den normalen System.out nach jedem Test sicher wieder her
        System.setOut(standardOut);
    }

    /**
     * Erfüllt Anforderung 4: Testet einen vollständigen Anwendungsfall von System.in bis System.out.
     * Nutzt Mockito.doAnswer, um die Antwort der GL zu simulieren, wodurch der Kreis geschlossen wird.
     */
    @Test
    void testFullCycleFromInputToOutputForCustomerInsertion() {
        // Leitet System.out in den Captor um (nur dieser Test prüft die Konsolenausgabe)
        System.setOut(new PrintStream(outputStreamCaptor));

        // 1. Mocking der Geschäftslogik
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        ConsoleClient client = new ConsoleClient(mockListener);

        // 2. Mockito anweisen: Wenn onInsertCustomer aufgerufen wird, sende sofort ein Feedback an den Client zurück!
        Mockito.doAnswer(invocation -> {
            client.onFeedbackReceived("Erfolg: Kunde Alice angelegt");
            return null; // void Methode
        }).when(mockListener).onInsertCustomer("Alice");

        // 3. Simuliere Benutzereingabe (System.in)
        String simulatedInput = ":c\nAlice\n:x\n";
        Scanner testScanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // 4. Ausführung (Act)
        client.start(testScanner);

        // 5. GENAU EINE ZUSICHERUNG (Assert): Prüft, ob das Feedback am Ende auf der Konsole (System.out) gedruckt wurde
        assertTrue(outputStreamCaptor.toString().trim().contains("Erfolg: Kunde Alice angelegt"));
    }

    /**
     * Stellvertreter-Test (Prototyp 5): Der Befehl 'save JOS' im Persistenzmodus
     * muss das onSave-Event mit der gewählten Technologie auslösen.
     */
    @Test
    void testSaveCommandTriggersOnSaveEvent() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        PersistenceCommandListener mockPersistence = Mockito.mock(PersistenceCommandListener.class);
        ConsoleClient client = new ConsoleClient(mockListener);
        client.setPersistenceListener(mockPersistence);

        String simulatedInput = ":p\nsave JOS\n:x\n";
        client.start(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        Mockito.verify(mockPersistence).onSave("JOS");
    }

    /**
     * Stellvertreter-Test (Prototyp 5): Der Befehl 'load JBP' im Persistenzmodus
     * muss das onLoad-Event mit der gewählten Technologie auslösen.
     */
    @Test
    void testLoadCommandTriggersOnLoadEvent() {
        CargoCommandListener mockListener = Mockito.mock(CargoCommandListener.class);
        PersistenceCommandListener mockPersistence = Mockito.mock(PersistenceCommandListener.class);
        ConsoleClient client = new ConsoleClient(mockListener);
        client.setPersistenceListener(mockPersistence);

        String simulatedInput = ":p\nload JBP\n:x\n";
        client.start(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes())));

        Mockito.verify(mockPersistence).onLoad("JBP");
    }
}