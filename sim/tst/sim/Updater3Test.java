package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Testklasse für den Updater-Thread (Simulation 3).
 * Überprüft die deterministische Funktionalität (Aktualisierung des Inspektionsdatums)
 * ohne den Einsatz von Schleifen und unter Einhaltung der Regel: Nur eine Zusicherung pro Test.
 */
class Updater3Test {

    /**
     * Testet, ob der Updater3 das Inspektionsdatum eines vorhandenen Frachtstücks aktualisiert.
     * Um den Zufallsgenerator im Thread deterministisch zu machen, wird nur ein einziger
     * simulierter Lagerplatz zurückgegeben.
     */
    @Test
    void testUpdater3UpdatesInspectionCorrectly() {
        // Arrange: Mock und Monitor erstellen
        WarehouseManager glMock = mock(WarehouseManager.class);
        Object monitor = new Object();

        // Deterministisch: Lager enthält nur ein Element, daher MUSS der Random Platz 99 wählen
        when(glMock.getStorageLocations()).thenReturn(List.of(99));

        // Endlosschleife abbrechen, sobald das Update aufgerufen wird
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onUpdateInspectionDate(99);

        // Act: Updater3 initialisieren und ausführen
        Updater3 updater3 = new Updater3(glMock, monitor);
        updater3.run();

        // Assert: Genau eine Zusicherung (Verify)
        verify(glMock).onUpdateInspectionDate(99);
    }
}