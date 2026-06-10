package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Testklasse für den Konsumenten (Simulation 1 & 2).
 * Überprüft die deterministische Funktionalität ohne den Einsatz von Schleifen
 * und unter Einhaltung der Regel: Nur eine Zusicherung (Assert/Verify) pro Test.
 */
class KonsumentTest {

    /**
     * Testet, ob der Konsument ein Frachtstück korrekt aus der
     * Geschäftslogik (WarehouseManager) löscht.
     * Um die Endlosschleife des Threads zu durchbrechen, wird der Thread
     * beim Aufruf der Lösch-Methode unterbrochen (interrupted).
     */
    @Test
    void testKonsumentDeletesCargoCorrectly() {
        // Arrange: Mock erstellen (Keine Impl-Klassen erlauben)
        WarehouseManager glMock = mock(WarehouseManager.class);

        // Deterministisches Verhalten erzwingen: Das Lager meldet genau einen belegten Platz (42)
        when(glMock.getStorageLocations()).thenReturn(List.of(42));

        // Endlosschleife des Konsumenten beim Aufruf von onDeleteCargo abbrechen
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onDeleteCargo(42);

        // Act: Konsument initialisieren und ausführen
        Konsument konsument = new Konsument(glMock);
        konsument.run();

        // Assert: Genau eine Zusicherung (Verify), dass die Methode mit dem richtigen Parameter aufgerufen wurde
        verify(glMock).onDeleteCargo(42);
    }
}