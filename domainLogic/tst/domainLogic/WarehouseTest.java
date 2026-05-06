package domainLogic;

import administration.Customer;
import cargo.Hazard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WarehouseTest {

    // Testet ob man einen Kunden erfolgreich zum  hinzufügen kann
    @Test
    public void testAddCustomer() {
        WarehouseManager manager = new WarehouseManager(10);
        Customer customer = new CustomerImpl("Max Mustermann");

        boolean result = manager.addCustomer(customer);
        assertTrue(result);
    }

    // Testet ob man den gleichen Kunden zweimal hinzufügen kann (sollte false sein)
    @Test
    public void testAddDuplicateCustomer() {
        WarehouseManager manager = new WarehouseManager(10);
        Customer c1 = new CustomerImpl("Ahmed");
        Customer c2 = new CustomerImpl("Ahmed");

        boolean firstAdd = manager.addCustomer(c1);
        boolean secondAdd = manager.addCustomer(c2);

        assertTrue(firstAdd);
        assertFalse(secondAdd);
    }

    // Testet das Einfügen von Fracht wenn der Kunde registriert ist
    @Test
    public void testInsertCargoSuccess() {
        WarehouseManager manager = new WarehouseManager(5);
        Customer customer = new CustomerImpl("Ali");
        manager.addCustomer(customer);

        Collection<Hazard> hazards = new ArrayList<>();
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("100.0"), hazards, 10);

        boolean inserted = manager.insertCargo(cargo);
        assertTrue(inserted);
    }

    // Testet ob das Einfügen fehlschlägt wenn die Kapazität voll ist
    @Test
    public void testInsertCargoFullCapacity() {
        WarehouseManager manager = new WarehouseManager(0); // Kapazität ist 0
        Customer customer = new CustomerImpl("Sara");
        manager.addCustomer(customer);

        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("50.0"), null, 5);

        boolean result = manager.insertCargo(cargo);
        assertFalse(result); // Sollte false sein weil kein Platz da ist
    }

    // Ein Test mit Mockito um die Anforderungen (Stellvertretertests) zu erfüllen
    @Test
    public void testWithMockito() {
        WarehouseManager manager = new WarehouseManager(10);

        // Mock Objekt erstellen
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getName()).thenReturn("MockedCustomer");

        // Den Mock-Kunden hinzufügen
        boolean added = manager.addCustomer(mockCustomer);
        assertTrue(added);
        verify(mockCustomer, atLeastOnce());
    }
    // Testet ob die Getter-Methoden die richtigen Werte zurückgeben
    @Test
    public void testCargoGetters() {
        // Vorbereitung (Setup)
        Customer owner = new CustomerImpl("Max");
        java.math.BigDecimal value = new java.math.BigDecimal("500.0");
        java.util.Collection<cargo.Hazard> hazards = new java.util.HashSet<>();
        int grainSize = 20;

        // Objekt erstellen
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(owner, value, hazards, grainSize);

        // Überprüfung (Assertions)
        assertEquals(owner, cargo.getOwner());
        assertEquals(value, cargo.getValue());
        assertEquals(grainSize, cargo.getGrainSize());
        assertNotNull(cargo.getHazards());

        // Test für den StorageLocation Setter/Getter
        cargo.setStorageLocation(5);
        assertEquals(5, cargo.getStorageLocation());
    }

    // Testet die Berechnung der Lagerdauer (nicht null nach Erstellung)
    @Test
    public void testDurationOfStorage() {
        Customer owner = new CustomerImpl("Tom");
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(owner, new java.math.BigDecimal("10.0"), null, 1);

        // Die Dauer sollte nicht null sein, wenn das Objekt gerade erst erstellt wurde
        assertNotNull(cargo.getDurationOfStorage());
    }
}