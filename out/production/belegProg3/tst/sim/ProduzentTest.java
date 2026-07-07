package sim;

import events.CargoCommandListener;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Testklasse für den Produzenten (Simulation).
 * Überprüft die deterministische Funktionalität ohne den Einsatz von Schleifen
 * und unter Einhaltung der Regel: Nur eine Zusicherung (Assert/Verify) pro Test.
 */
class ProduzentTest {

    /**
     * Testet, ob der Produzent ein generiertes Frachtstück korrekt in die
     * Geschäftslogik (WarehouseManager) einfügt.
     * Um die Endlosschleife des Threads zu durchbrechen, wird der Thread
     * nach dem ersten Aufruf der Einfüge-Methode unterbrochen (interrupted).
     */
    @Test
    void testProduzentInsertsCargoCorrectly() {
        // Arrange: Mocks erstellen (Keine Impl-Klassen erlaubt)
        CargoCommandListener glMock = mock(CargoCommandListener.class);
        CargoGenerator generatorMock = mock(CargoGenerator.class);

        // Deterministische Testdaten vorbereiten
        CargoData dummyData = new CargoData(
                "UnitisedCargo",
                "Alice",
                BigDecimal.TEN,
                Collections.emptyList(),
                true,
                false,
                0
        );

        when(generatorMock.generate()).thenReturn(dummyData);

        // Endlosschleife des Produzenten beim ersten Methodenaufruf abbrechen
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onInsertCargo(anyString(), anyString(), any(), any(), anyBoolean(), anyBoolean(), anyInt());

        // Act: Produzent initialisieren und ausführen
        Produzent produzent = new Produzent(glMock, generatorMock);
        produzent.run();

        // Assert: Genau eine Zusicherung (Verify), dass die Methode mit den richtigen Parametern aufgerufen wurde
        verify(glMock).onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, Collections.emptyList(), true, false, 0);
    }
}