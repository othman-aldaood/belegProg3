package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;
import java.util.Arrays; // تأكد من وجود هذا الـ Import
import static org.mockito.Mockito.*;

class Konsument3Test {

    @Test
    void testKonsument3DeletesOldestInspectionCargo() {
        WarehouseManager glMock = mock(WarehouseManager.class);
        Object monitor = new Object();

        // التعديل هنا:
        when(glMock.getStorageLocations()).thenReturn(Arrays.asList(7));

        when(glMock.getOldestInspectionLocation()).thenReturn(7);

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onDeleteCargo(7);

        Konsument3 konsument3 = new Konsument3(glMock, monitor);
        konsument3.run();

        verify(glMock).onDeleteCargo(7);
    }
}