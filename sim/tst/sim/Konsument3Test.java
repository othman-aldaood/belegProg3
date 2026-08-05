package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class Konsument3Test {

    @Test
    void testKonsument3DeletesOldestInspectionCargo() {
        WarehouseManager glMock = mock(WarehouseManager.class);
        Object monitor = new Object();

        // Der Konsument ermittelt das aelteste Inspektionsdatum selbst
        Map<Integer, Date> inspections = new HashMap<>();
        inspections.put(7, new Date(1000L));
        inspections.put(8, new Date(2000L));
        when(glMock.getInspectionDatesMap()).thenReturn(inspections);

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onDeleteCargo(7);

        Konsument3 konsument3 = new Konsument3(glMock, monitor);
        konsument3.run();

        verify(glMock).onDeleteCargo(7);
    }
}