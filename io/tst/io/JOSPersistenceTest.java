package io;

import domainLogic.WarehouseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Stellvertreter-Tests für die JOS-Persistenz.
 * Streng nach den Testvorgaben: eine Zusicherung pro Test, keine Schleifen,
 * kein Dateisystemzugriff (in-memory Streams), Mockito statt Impl-Klassen.
 */
class JOSPersistenceTest {

    private PersistenceStrategy josPersistence;
    private WarehouseManager mockManager;

    @BeforeEach
    void setUp() {
        this.josPersistence = new JOSPersistence();
        // Serialisierbarer Stellvertreter der Geschäftslogik
        this.mockManager = mock(WarehouseManager.class, withSettings().serializable());
    }

    @Test
    void saveWritesDataToStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.josPersistence.save(this.mockManager, out);

        assertTrue(out.size() > 0);
    }

    @Test
    void loadReturnsManagerFromStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.josPersistence.save(this.mockManager, out);

        WarehouseManager loaded = this.josPersistence.load(new ByteArrayInputStream(out.toByteArray()));

        assertNotNull(loaded);
    }
}
