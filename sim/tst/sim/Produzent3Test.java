package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Testklasse für den Produzenten der Simulation 3.
 * Deterministisch: Generator und Geschäftslogik werden gemockt, die
 * Endlosschleife wird beim ersten Einfügen unterbrochen.
 */
class Produzent3Test {

    @Test
    void testProduzent3InsertsCargoCorrectly() {
        WarehouseManager glMock = mock(WarehouseManager.class);
        CargoGenerator generatorMock = mock(CargoGenerator.class);
        Object monitor = new Object();

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
        when(glMock.getCapacity()).thenReturn(10);

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onInsertCargo(anyString(), anyString(), any(), any(), anyBoolean(), anyBoolean(), anyInt());

        Produzent3 produzent3 = new Produzent3(glMock, generatorMock, monitor);
        produzent3.run();

        verify(glMock).onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, Collections.emptyList(), true, false, 0);
    }
}
