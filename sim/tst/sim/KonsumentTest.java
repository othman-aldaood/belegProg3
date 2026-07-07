package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;
import java.util.Arrays; // تأكد من وجود هذا الـ Import
import static org.mockito.Mockito.*;

class KonsumentTest {

    @Test
    void testKonsumentDeletesCargoCorrectly() {
        WarehouseManager glMock = mock(WarehouseManager.class);

        // التعديل هنا:
        when(glMock.getStorageLocations()).thenReturn(Arrays.asList(42));

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onDeleteCargo(42);

        Konsument konsument = new Konsument(glMock);
        konsument.run();

        verify(glMock).onDeleteCargo(42);
    }
}