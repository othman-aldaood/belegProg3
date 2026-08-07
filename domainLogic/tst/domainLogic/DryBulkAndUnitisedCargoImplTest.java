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
 * Unittests fuer DryBulkAndUnitisedCargoImpl.
 */
public class DryBulkAndUnitisedCargoImplTest {

    @Test
    public void defaultKonstruktorErzeugtLeereGefahrenstoffe() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void konstruktorSetztEigentuemerin() {
        CustomerImpl alice = new CustomerImpl("Alice");
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(alice, BigDecimal.ONE, null, 5, true);
        assertEquals(alice, cargo.getOwner());
    }

    @Test
    public void konstruktorSetztWert() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.TEN, null, 5, true);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void konstruktorSetztGrainSize() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 7, false);
        assertEquals(7, cargo.getGrainSize());
    }

    @Test
    public void konstruktorSetztFragile() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5, true);
        assertTrue(cargo.isFragile());
    }

    @Test
    public void konstruktorUebernimmtGefahrenstoffe() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.FLAMMABLE), 5, false);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setOwnerAendertEigentuemerin() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        CustomerImpl bob = new CustomerImpl("Bob");
        cargo.setOwner(bob);
        assertEquals(bob, cargo.getOwner());
    }

    @Test
    public void setInsertionDateAendertEinfuegedatum() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        Date date = new Date(1000L);
        cargo.setInsertionDate(date);
        assertEquals(date, cargo.getInsertionDate());
    }

    @Test
    public void getDurationOfStorageLiefertNullOhneEinfuegedatum() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setInsertionDate(null);
        assertNull(cargo.getDurationOfStorage());
    }

    @Test
    public void getDurationOfStorageIstNichtNegativ() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        assertFalse(cargo.getDurationOfStorage().isNegative());
    }

    @Test
    public void setLastInspectionDateAendertInspektionsdatum() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        Date date = new Date(2000L);
        cargo.setLastInspectionDate(date);
        assertEquals(date, cargo.getLastInspectionDate());
    }

    @Test
    public void setStorageLocationAendertLagerplatz() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setStorageLocation(42);
        assertEquals(42, cargo.getStorageLocation());
    }

    @Test
    public void setValueAendertWert() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setValue(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void getHazardsLiefertKopie() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), 5, false);
        cargo.getHazards().clear();
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsUebernimmtGefahrenstoffe() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        Collection<Hazard> hazards = new HashSet<>(Collections.singletonList(Hazard.RADIOACTIVE));
        cargo.setHazards(hazards);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsMitNullErzeugtLeereMenge() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), 5, false);
        cargo.setHazards(null);
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void setGrainSizeAendertKoernung() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setGrainSize(9);
        assertEquals(9, cargo.getGrainSize());
    }

    @Test
    public void setFragileAendertFragile() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setFragile(true);
        assertTrue(cargo.isFragile());
    }

    @Test
    public void copySetztDenUebergebenenLagerplatz() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"),
                BigDecimal.ONE, null, 5, true);
        assertEquals(7, cargo.copy(7).getStorageLocation());
    }

    @Test
    public void copyKopiertGrainSize() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"),
                BigDecimal.ONE, null, 5, true);
        DryBulkAndUnitisedCargoImpl copy = cargo.copy(7);
        assertEquals(5, copy.getGrainSize());
    }

    @Test
    public void copyKopiertFragile() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"),
                BigDecimal.ONE, null, 5, true);
        DryBulkAndUnitisedCargoImpl copy = cargo.copy(7);
        assertTrue(copy.isFragile());
    }

    @Test
    public void copyKopiertEigentuemerinUnabhaengig() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl(new CustomerImpl("Alice"),
                BigDecimal.ONE, null, 5, true);
        cargo.copy(7).getOwner().setName("Hacked");
        assertEquals("Alice", cargo.getOwner().getName());
    }

    @Test
    public void copyOhneEigentuemerinLiefertNullEigentuemerin() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        assertNull(cargo.copy(7).getOwner());
    }

    @Test
    public void copyUebernimmtDasEinfuegedatum() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setInsertionDate(new Date(1000L));
        assertEquals(new Date(1000L), cargo.copy(7).getInsertionDate());
    }

    @Test
    public void copyOhneEinfuegedatumLiefertNull() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setInsertionDate(null);
        assertNull(cargo.copy(7).getInsertionDate());
    }

    @Test
    public void copyUebernimmtDasInspektionsdatum() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        cargo.setLastInspectionDate(new Date(2000L));
        assertEquals(new Date(2000L), cargo.copy(7).getLastInspectionDate());
    }

    @Test
    public void copyOhneInspektionsdatumLiefertNull() {
        DryBulkAndUnitisedCargoImpl cargo = new DryBulkAndUnitisedCargoImpl();
        assertNull(cargo.copy(7).getLastInspectionDate());
    }
}
