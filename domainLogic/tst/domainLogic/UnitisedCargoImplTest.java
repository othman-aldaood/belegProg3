package domainLogic;

import cargo.Hazard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unittests fuer UnitisedCargoImpl.
 */
public class UnitisedCargoImplTest {

    @Test
    public void defaultKonstruktorErzeugtLeereGefahrenstoffe() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void konstruktorSetztEigentuemerin() {
        CustomerImpl alice = new CustomerImpl("Alice");
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(alice, BigDecimal.ONE, null, true);
        assertEquals(alice, cargo.getOwner());
    }

    @Test
    public void konstruktorSetztWert() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.TEN, null, true);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void konstruktorSetztFragile() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true);
        assertTrue(cargo.isFragile());
    }

    @Test
    public void konstruktorUebernimmtGefahrenstoffe() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.EXPLOSIVE), false);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setFragileAendertFragile() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setFragile(true);
        assertTrue(cargo.isFragile());
    }

    @Test
    public void setOwnerAendertEigentuemerin() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        CustomerImpl bob = new CustomerImpl("Bob");
        cargo.setOwner(bob);
        assertEquals(bob, cargo.getOwner());
    }

    @Test
    public void setInsertionDateAendertEinfuegedatum() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        Date date = new Date(1000L);
        cargo.setInsertionDate(date);
        assertEquals(date, cargo.getInsertionDate());
    }

    @Test
    public void getDurationOfStorageLiefertNullDauerOhneEinfuegedatum() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setInsertionDate(null);
        assertEquals(Duration.ZERO, cargo.getDurationOfStorage());
    }

    @Test
    public void getDurationOfStorageIstNichtNegativ() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        assertFalse(cargo.getDurationOfStorage().isNegative());
    }

    @Test
    public void setLastInspectionDateAendertInspektionsdatum() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        Date date = new Date(2000L);
        cargo.setLastInspectionDate(date);
        assertEquals(date, cargo.getLastInspectionDate());
    }

    @Test
    public void setStorageLocationAendertLagerplatz() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setStorageLocation(42);
        assertEquals(42, cargo.getStorageLocation());
    }

    @Test
    public void setValueAendertWert() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setValue(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, cargo.getValue());
    }

    @Test
    public void getHazardsLiefertKopie() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), false);
        cargo.getHazards().clear();
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsUebernimmtGefahrenstoffe() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        Collection<Hazard> hazards = new HashSet<>(Collections.singletonList(Hazard.RADIOACTIVE));
        cargo.setHazards(hazards);
        assertEquals(1, cargo.getHazards().size());
    }

    @Test
    public void setHazardsMitNullErzeugtLeereMenge() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.TOXIC), false);
        cargo.setHazards(null);
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void copySetztDenUebergebenenLagerplatz() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true);
        assertEquals(7, cargo.copy(7).getStorageLocation());
    }

    @Test
    public void copyKopiertFragile() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true);
        UnitisedCargoImpl copy = cargo.copy(7);
        assertTrue(copy.isFragile());
    }

    @Test
    public void copyKopiertEigentuemerinUnabhaengig() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true);
        cargo.copy(7).getOwner().setName("Hacked");
        assertEquals("Alice", cargo.getOwner().getName());
    }

    @Test
    public void copyOhneEigentuemerinLiefertNullEigentuemerin() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        assertNull(cargo.copy(7).getOwner());
    }

    @Test
    public void copyUebernimmtDasEinfuegedatum() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setInsertionDate(new Date(1000L));
        assertEquals(new Date(1000L), cargo.copy(7).getInsertionDate());
    }

    @Test
    public void copyOhneEinfuegedatumLiefertNull() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setInsertionDate(null);
        assertNull(cargo.copy(7).getInsertionDate());
    }

    @Test
    public void copyUebernimmtDasInspektionsdatum() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        cargo.setLastInspectionDate(new Date(2000L));
        assertEquals(new Date(2000L), cargo.copy(7).getLastInspectionDate());
    }

    @Test
    public void copyOhneInspektionsdatumLiefertNull() {
        UnitisedCargoImpl cargo = new UnitisedCargoImpl();
        assertNull(cargo.copy(7).getLastInspectionDate());
    }
}
