package gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
class CustomerViewModelTest {
    /**
     *
     */
    @Test
    void testNamePropertyGibtKorrektenWertZurueck() {
        CustomerViewModel viewModel = new CustomerViewModel("Bob", 3);
        assertEquals("Bob", viewModel.nameProperty().get());
    }

    /**
     *
     */
    @Test
    void testAnzahlFrachtstueckePropertyGibtKorrektenWertZurueck() {
        CustomerViewModel viewModel = new CustomerViewModel("Bob", 3);
        assertEquals(3, viewModel.anzahlFrachtstueckeProperty().get());
    }
}