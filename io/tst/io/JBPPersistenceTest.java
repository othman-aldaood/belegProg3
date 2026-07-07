package io;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die JBP-Persistenz.
 * Streng nach den Testvorgaben: eine Zusicherung pro Test, keine Schleifen,
 * kein Dateisystemzugriff (in-memory Streams).
 * Hinweis: XMLEncoder benötigt eine echte Bean-Klasse mit Standardkonstruktor,
 * daher wird hier die produktive Klasse WarehouseManager statt eines Mocks verwendet.
 */
class JBPPersistenceTest {

    private PersistenceStrategy jbpPersistence;
    private WarehouseManager manager;

    @BeforeEach
    void setUp() {
        this.jbpPersistence = new JBPPersistence();
        this.manager = new WarehouseManager(7);
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
}
