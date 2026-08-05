package io;

import cargo.Cargo;
import domainLogic.WarehouseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die JBP-Persistenz mit echtem Speicher-/Lade-Roundtrip.
 * Streng nach den Testvorgaben: eine Zusicherung pro Test, keine Schleifen,
 * kein Dateisystemzugriff (in-memory Streams), keine Hilfsmethoden
 * (Duplikate sind laut Vorgaben zulässig).
 * Hinweis: XMLEncoder benötigt echte Bean-Klassen mit Standardkonstruktor,
 * daher wird die produktive Klasse WarehouseManager statt eines Mocks verwendet.
 */
class JBPPersistenceTest {

    private PersistenceStrategy jbpPersistence;
    private WarehouseManager manager;

    @BeforeEach
    void setUp() {
        this.jbpPersistence = new JBPPersistence();
        this.manager = new WarehouseManager(7);
        this.manager.onInsertCustomer("Alice");
        this.manager.onInsertCargo("DryBulkCargo", "Alice", new BigDecimal("1500.50"),
                Collections.singletonList("flammable"), false, false, 5);
    }

    @Test
    void saveWritesDataToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.jbpPersistence.save(this.manager, out);

        assertTrue(out.size() > 0);
    }

    @Test
    void loadRestoresCapacityFromStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(7, loaded.getCapacity());
    }

    @Test
    void loadRestoresCustomers() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(1, loaded.getAllCustomers().size());
    }

    @Test
    void loadRestoresCargos() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(1, loaded.getCurrentSize());
    }

    @Test
    void loadRestoresInsertionDate() throws Exception {
        // Das Einfügedatum darf beim JBP-Roundtrip nicht verloren gehen.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(this.manager.getInsertionDatesMap().get(1), loaded.getInsertionDatesMap().get(1));
    }

    @Test
    void loadRestoresStorageLocation() throws Exception {
        // Der Lagerplatz darf beim JBP-Roundtrip nicht verloren gehen.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(1, loaded.getAllCargos().iterator().next().getStorageLocation());
    }

    @Test
    void loadRestoresHazards() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        Cargo cargo = loaded.getAllCargos().iterator().next();
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    void loadRestoresWert() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(new BigDecimal("1500.50"), loaded.getAllCargos().iterator().next().getValue());
    }

    @Test
    void loadRestoresNextLocation() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.jbpPersistence.save(this.manager, out);

        WarehouseManager loaded = this.jbpPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(2, loaded.getNextLocation());
    }
}
