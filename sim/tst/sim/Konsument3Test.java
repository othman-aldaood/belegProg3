package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * Testklasse für den modifizierten Konsumenten (Simulation 3).
 * Überprüft die deterministische Funktionalität (Löschen des ältesten Frachtstücks)
 * ohne den Einsatz von Schleifen und unter Einhaltung der Regel: Nur eine Zusicherung pro Test.
 */
class Konsument3Test {

    /**
     * Testet, ob der Konsument3 korrekt das Frachtstück mit dem ältesten
     * Inspektionsdatum ermittelt und anschließend löscht.
     * Die Endlosschleife wird durch einen Interrupt beim Löschvorgang unterbrochen.
     */
    @Test
    void testKonsument3DeletesOldestInspectionCargo() {
        // Arrange: Mock und Monitor erstellen
        WarehouseManager glMock = mock(WarehouseManager.class);
        Object monitor = new Object();

        // Deterministisches Verhalten erzwingen: Lager ist nicht leer (verhindert wait())
        when(glMock.getStorageLocations()).thenReturn(List.of(7));

        // Deterministisch: Das älteste Frachtstück befindet sich auf Platz 7
        when(glMock.getOldestInspectionLocation()).thenReturn(7);

        // Endlosschleife abbrechen, sobald die Lösch-Methode aufgerufen wird
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onDeleteCargo(7);

        // Act: Konsument3 initialisieren und ausführen
        Konsument3 konsument3 = new Konsument3(glMock, monitor);
        konsument3.run();

        // Assert: Genau eine Zusicherung (Verify)
        verify(glMock).onDeleteCargo(7);
    }
}