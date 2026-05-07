package domainLogic;

import administration.Customer;
import cargo.Hazard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WarehouseTest {

    private WarehouseManager manager;

    // Diese Methode bereitet die Testumgebung vor jedem Testlauf vor
    @BeforeEach
    public void setUp() {
        // Wir erstellen einen Manager mit Platz für 10 Gegenstände
        manager = new WarehouseManager(10);
    }

    // --- Tests für die Kundenverwaltung ---

    @Test
    public void testAddCustomer() {
        Customer customer = new CustomerImpl("Max Mustermann");
        assertTrue(manager.addCustomer(customer));
    }

    @Test
    public void testAddDuplicateCustomer() {
        Customer c1 = new CustomerImpl("Ahmed");
        Customer c2 = new CustomerImpl("Ahmed");

        manager.addCustomer(c1);
        // Da die Namen gleich sind (equals/hashCode), sollte das zweite Mal false sein
        assertFalse(manager.addCustomer(c2));
    }

    @Test
    public void testRemoveCustomer() {
        Customer customer = new CustomerImpl("Lisa");
        manager.addCustomer(customer);

        // Testet das Löschen eines Kunden
        assertTrue(manager.removeCustomer(customer));
        assertFalse(manager.getAllCustomers().contains(customer));
    }

    // --- Tests für das Einfügen (Pfadabdeckung / Path Coverage) ---

    // Pfad 1: Erfolgreiches Einfügen von Schüttgut
    @Test
    public void testInsertDryBulkCargoSuccess() {
        Customer customer = new CustomerImpl("Ali");
        manager.addCustomer(customer);

        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("100.0"), null, 10);
        assertTrue(manager.insertCargo(cargo));
        assertEquals(1, cargo.getStorageLocation());
    }

    // Pfad 2: Erfolgreiches Einfügen von Stückgut (Zweiter Frachttyp)
    @Test
    public void testInsertUnitisedCargoSuccess() {
        Customer customer = new CustomerImpl("Tom");
        manager.addCustomer(customer);

        // Wir nutzen hier den zweiten Frachttyp inkl. 'fragile' Parameter
        UnitisedCargoImpl cargo = new UnitisedCargoImpl(customer, new BigDecimal("200.0"), null, true);
        assertTrue(manager.insertCargo(cargo));
    }

    // Pfad 3: Einfügen schlägt fehl, wenn Kapazität voll ist
    @Test
    public void testInsertCargoFullCapacity() {
        WarehouseManager smallManager = new WarehouseManager(0); // Kapazität ist 0
        Customer customer = new CustomerImpl("Sara");
        smallManager.addCustomer(customer);

        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("50.0"), null, 5);
        assertFalse(smallManager.insertCargo(cargo));
    }

    // Pfad 4: Einfügen schlägt fehl, wenn der Kunde nicht registriert ist
    @Test
    public void testInsertCargoCustomerNotRegistered() {
        Customer unregistered = new CustomerImpl("Gast");
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(unregistered, new BigDecimal("50.0"), null, 5);

        // Da der Kunde nicht mit manager.addCustomer hinzugefügt wurde
        assertFalse(manager.insertCargo(cargo));
    }

    // --- Stellvertretertests mit Mockito (Mindestens zwei erforderlich) ---

    @Test
    public void testMockitoVerifyCustomerName() {
        Customer mockCustomer = mock(Customer.class);
        when(mockCustomer.getName()).thenReturn("MockedUser");

        manager.addCustomer(mockCustomer);

        // Wir prüfen ob das Mock-Objekt richtig interagiert
        assertEquals("MockedUser", mockCustomer.getName());
        verify(mockCustomer, atLeastOnce()).getName();
    }

    @Test
    public void testMockitoWithManagerInteraction() {
        // Ein zweiter Test mit Mockito für die Anforderung
        Customer mockCustomer = mock(Customer.class);

        boolean added = manager.addCustomer(mockCustomer);
        assertTrue(added);

        // Wir verifizieren, dass der Manager den Mock-Kunden löschen kann
        assertTrue(manager.removeCustomer(mockCustomer));
    }

    /// Testet ob die Gefahrenstoffe korrekt erkannt werden
    @Test
    public void testGetPresentHazards() {
        Customer customer = new CustomerImpl("HazardUser");
        manager.addCustomer(customer);

        HashSet<Hazard> hazards = new HashSet<>();

        // Versuche es mit GROSSBUCHSTABEN (Java Standard für Enums)
        hazards.add(Hazard.EXPLOSIVE);

        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("100"), hazards, 5);
        manager.insertCargo(cargo);

        // Überprüfung
        assertTrue(manager.getPresentHazards().contains(Hazard.EXPLOSIVE));
    }

    @Test
    public void testUpdateInspectionDate() {
        Customer customer = new CustomerImpl("DateUser");
        manager.addCustomer(customer);
        DryBulkCargoImpl cargo = new DryBulkCargoImpl(customer, new BigDecimal("10"), null, 1);
        manager.insertCargo(cargo);

        java.util.Date now = new java.util.Date();
        assertTrue(manager.updateInspectionDate(1, now));
        assertEquals(now, cargo.getLastInspectionDate());
    }
}