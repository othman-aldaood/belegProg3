package sim;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.mockito.Mockito.*;

class Updater3Test {

    @Test
    void testUpdater3UpdatesInspectionCorrectly() {
        WarehouseManager glMock = mock(WarehouseManager.class);

        when(glMock.getStorageLocations()).thenReturn(Arrays.asList(99));

        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(glMock).onUpdateInspectionDate(99);

        Updater3 updater3 = new Updater3(glMock);
        updater3.run();

        verify(glMock).onUpdateInspectionDate(99);
    }
}