package cli;

import events.CargoCommandListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        // Leitet System.out in unseren Captor um, um die Konsolenausgabe zu prüfen
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // Stellt den normalen System.out nach jedem Test wieder her
        System.setOut(standardOut);
    }

    /**
     * Erfüllt Anforderung 4: Testet einen vollständigen Anwendungsfall von System.in bis System.out.
     * Nutzt Mockito.doAnswer, um die Antwort der GL zu simulieren, wodurch der Kreis geschlossen wird.
     */
    @Test
    void testFullCycleFromInputToOutputForCustomerInsertion() {
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
}