package gui;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CargoViewModelTest {

    @Test
    void testLagerplatzPropertyGibtKorrektenWertZurueck() {
        CargoViewModel viewModel = new CargoViewModel(10, "Alice", new Date(), Duration.ofDays(5));
        assertEquals(10, viewModel.lagerplatzProperty().get());
    }

    @Test
    void testKundenNamePropertyGibtKorrektenWertZurueck() {
        CargoViewModel viewModel = new CargoViewModel(10, "Alice", new Date(), Duration.ofDays(5));
        assertEquals("Alice", viewModel.kundenNameProperty().get());
    }

    @Test
    void testInspektionsDatumPropertyGibtKorrektenWertZurueck() {
        Date testDatum = new Date();
        CargoViewModel viewModel = new CargoViewModel(10, "Alice", testDatum, Duration.ofDays(5));
        assertEquals(testDatum, viewModel.inspektionsDatumProperty().get());
    }

    @Test
    void testEinlagerungsDauerPropertyGibtKorrektenWertZurueck() {
        Duration testDauer = Duration.ofDays(5);
        CargoViewModel viewModel = new CargoViewModel(10, "Alice", new Date(), testDauer);
        assertEquals(testDauer, viewModel.einlagerungsDauerProperty().get());
    }
}