package io;

import domainLogic.WarehouseManager;
import events.ChangeObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests für die JOS-Persistenz mit echtem Speicher-/Lade-Roundtrip.
 * Streng nach den Testvorgaben: eine Zusicherung pro Test, keine Schleifen,
 * kein Dateisystemzugriff (in-memory Streams), keine Hilfsmethoden
 * (Duplikate sind laut Vorgaben zulässig).
 * Hinweis: JOS serialisiert den echten Objektgraphen; ein Mock würde das
 * eigentliche Speichern und Laden nicht testen, daher wird die produktive
 * Klasse WarehouseManager verwendet.
 */
class JOSPersistenceTest {

    private PersistenceStrategy josPersistence;
    private WarehouseManager manager;

    @BeforeEach
    void setUp() {
        this.josPersistence = new JOSPersistence();
        this.manager = new WarehouseManager(7);
        this.manager.onInsertCustomer("Alice");
        this.manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.TEN,
                Collections.singletonList("flammable"), false, false, 5);
    }

    @Test
    void saveWritesDataToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.josPersistence.save(this.manager, out);

        assertTrue(out.size() > 0);
    }

    @Test
    void loadRestoresCapacity() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(7, loaded.getCapacity());
    }

    @Test
    void loadRestoresCustomers() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(1, loaded.getAllCustomers().size());
    }

    @Test
    void loadRestoresCargos() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(1, loaded.getCurrentSize());
    }

    @Test
    void loadRestoresInsertionDate() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(this.manager.getInsertionDatesMap().get(1), loaded.getInsertionDatesMap().get(1));
    }

    @Test
    void loadRestoresNextLocation() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(2, loaded.getNextLocation());
    }

    @Test
    void loadedManagerAcceptsNewObservers() throws Exception {
        // Beobachter gehören nicht zum Zustand; nach dem Laden können neue
        // Beobachter registriert werden (deckt die transienten Listen ab).
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.manager, out);
        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);

        loaded.addChangeObserver(observer);
        loaded.onInsertCustomer("Bob");

        Mockito.verify(observer).onChanged();
    }
}
