package domainLogic;

import cargo.Hazard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unittests fuer DryBulkCargoImpl.
 */
public class DryBulkCargoImplTest {

    @Test
    public void defaultKonstruktorErzeugtLeereGefahrenstoffe() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void konstruktorSetztEigentuemerin() {
        CustomerImpl alice = new CustomerImpl("Alice");
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(alice, BigDecimal.ONE, null, 5);
        assertEquals(alice, cargo.getOwner());
    }

    @Test
    public void konstruktorSetztWert() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.TEN, null, 5);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void konstruktorSetztGrainSize() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 7);
        assertEquals(7, cargo.getGrainSize());
    }

    @Test
    public void konstruktorMitNullGefahrenstoffenErzeugtLeereMenge() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void konstruktorUebernimmtGefahrenstoffe() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.FLAMMABLE), 5);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setOwnerAendertEigentuemerin() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        CustomerImpl bob = new CustomerImpl("Bob");
        cargo.setOwner(bob);
        assertEquals(bob, cargo.getOwner());
    }

    @Test
    public void setInsertionDateAendertEinfuegedatum() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        Date date = new Date(1000L);
        cargo.setInsertionDate(date);
        assertEquals(date, cargo.getInsertionDate());
    }

    @Test
    public void getDurationOfStorageLiefertNullOhneEinfuegedatum() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        cargo.setInsertionDate(null);
        assertNull(cargo.getDurationOfStorage());
    }

    @Test
    public void getDurationOfStorageIstNichtNegativ() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        assertFalse(cargo.getDurationOfStorage().isNegative());
    }

    @Test
    public void setLastInspectionDateAendertInspektionsdatum() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        Date date = new Date(2000L);
        cargo.setLastInspectionDate(date);
        assertEquals(date, cargo.getLastInspectionDate());
    }

    @Test
    public void setStorageLocationAendertLagerplatz() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        cargo.setStorageLocation(42);
        assertEquals(42, cargo.getStorageLocation());
    }

    @Test
    public void setValueAendertWert() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        cargo.setValue(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void getHazardsLiefertKopie() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), 5);
        cargo.getHazards().clear();
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsUebernimmtGefahrenstoffe() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        Collection<Hazard> hazards = new HashSet<>(Collections.singletonList(Hazard.RADIOACTIVE));
        cargo.setHazards(hazards);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsMitNullErzeugtLeereMenge() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), 5);
        cargo.setHazards(null);
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void setGrainSizeAendertKoernung() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        cargo.setGrainSize(9);
        assertEquals(9, cargo.getGrainSize());
    }

    @Test
    public void copySetztDenUebergebenenLagerplatz() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        assertEquals(7, cargo.copy(7).getStorageLocation());
    }

    @Test
    public void copyKopiertGrainSize() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        DryBulkCargoImpl copy = cargo.copy(7);
        assertEquals(5, copy.getGrainSize());
    }

    @Test
    public void copyKopiertEigentuemerinUnabhaengig() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        cargo.copy(7).getOwner().setName("Hacked");
        assertEquals("Alice", cargo.getOwner().getName());
    }

    @Test
    public void copyOhneEigentuemerinLiefertNullEigentuemerin() {
        DryBulkCargoImpl cargo = new DryBulkCargoImpl();
        assertNull(cargo.copy(7).getOwner());
    }
}
