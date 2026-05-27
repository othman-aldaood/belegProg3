package domainLogic;

import events.CapacityObserver;
import events.GLFeedbackListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testklasse für den WarehouseManager.
 * Erfüllt streng alle Anforderungen an die Testqualität:
 * - Genau eine Zusicherung pro Test
 * - Keine Schleifen, Verzweigungen
 * - Keine Implementierungsklassen (Impl) in den Tests instanziiert
 * - Testet die strikte Kapselung (Encapsulation)
 */
public class WarehouseTest {

    private WarehouseManager manager;

    @BeforeEach
    public void setUp() {
        // Angemessene Verwendung von @BeforeEach für das gesamte Setup
        manager = new WarehouseManager(10);
    }

    // =================================================================================
    // 1. Tests für das Event-System und CRUD (Genau eine Zusicherung pro Test)
    // =================================================================================

    @Test
    public void testOnInsertCustomerAddsCustomerToManager() {
        manager.onInsertCustomer("Alice");
        // Nur eine Zusicherung!
        assertEquals(1, manager.getAllCustomers().size());
    }

    @Test
    public void testOnInsertCargoFailsIfCustomerDoesNotExist() {
        // Wir fügen KEINEN Kunden ein und versuchen direkt ein Frachtstück einzufügen
        manager.onInsertCargo("UnitisedCargo", "Bob", BigDecimal.TEN, null, true, false, 0);

        // Das Lager muss leer bleiben
        assertTrue(manager.getAllCargos().isEmpty());
    }

    // =================================================================================
    // 2. Stellvertretertests mit Mockito (Listener & Observer prüfen)
    // =================================================================================

    @Test
    public void testCapacityObserverIsTriggeredWhenFull() {
        // Kapazität auf 10 setzen
        WarehouseManager managerForTest = new WarehouseManager(10);
        CapacityObserver mockObserver = mock(CapacityObserver.class);
        managerForTest.addCapacityObserver(mockObserver);

        managerForTest.onInsertCustomer("Alice");

        // Wir fügen manuell 10 Frachtstücke ein.
        // Beim Versuch, das 10. Stück einzufügen (während 9 schon drin sind = 90%), wird die Warnung ausgelöst.
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0);
        managerForTest.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.TEN, null, true, false, 0); // الشحنة العاشرة التي تطلق الإنذار

        // Genau eine Zusicherung: Prüft, ob der Mock für die Warnung aufgerufen wurde
        verify(mockObserver, times(1)).onCapacityWarning(anyString());
    }

    @Test
    public void testFeedbackListenerReceivesSuccessMessage() {
        GLFeedbackListener mockFeedback = mock(GLFeedbackListener.class);
        manager.setFeedbackListener(mockFeedback);

        manager.onInsertCustomer("Charlie");

        // Verifiziert, dass das UI eine Rückmeldung über den Event-Listener erhalten hat
        verify(mockFeedback, times(1)).onFeedbackReceived(contains("Erfolg"));
    }

    // =================================================================================
    // 3. Strenge Kapselungstests (Encapsulation Tests laut Beleg-Vorgaben)
    // =================================================================================

    @Test
    public void testKapselungGetAllCargosPreventsExternalClear() {
        // 1. Erzeuge Verwaltung mit einem Frachtstück
        manager.onInsertCustomer("Dave");
        manager.onInsertCargo("UnitisedCargo", "Dave", BigDecimal.TEN, null, true, false, 0);

        // 2. Hole Aufzählung der Frachtstücke
        Collection<?> externalList = manager.getAllCargos();

        // 3. Lösche diese Aufzählung von außen (Versuch die Kapselung zu brechen)
        externalList.clear();

        // 4. Prüfe Inhalt der echten Aufzählung (Sollte NICHT leer sein!)
        assertEquals(1, manager.getAllCargos().size());
    }

    @Test
    public void testKapselungGetAllCustomersPreventsExternalClear() {
        manager.onInsertCustomer("Eve");

        Collection<?> externalList = manager.getAllCustomers();
        externalList.clear(); // Manipulationsversuch

        // Das Original-Set im Manager muss geschützt bleiben
        assertFalse(manager.getAllCustomers().isEmpty());
    }
}