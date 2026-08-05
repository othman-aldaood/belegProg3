package domainLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unittests fuer CustomerImpl.
 */
public class CustomerImplTest {

    @Test
    public void defaultKonstruktorErzeugtKundinOhneNamen() {
        CustomerImpl customer = new CustomerImpl();
        assertNull(customer.getName());
    }

    @Test
    public void konstruktorSetztDenNamen() {
        CustomerImpl customer = new CustomerImpl("Alice");
        assertEquals("Alice", customer.getName());
    }

    @Test
    public void setNameAendertDenNamen() {
        CustomerImpl customer = new CustomerImpl("Alice");
        customer.setName("Bob");
        assertEquals("Bob", customer.getName());
    }

    @Test
    public void equalsLiefertTrueFuerDasselbeObjekt() {
        CustomerImpl customer = new CustomerImpl("Alice");
        assertTrue(customer.equals(customer));
    }

    @Test
    public void equalsLiefertTrueFuerGleichenNamen() {
        CustomerImpl a = new CustomerImpl("Alice");
        CustomerImpl b = new CustomerImpl("Alice");
        assertTrue(a.equals(b));
    }

    @Test
    public void equalsLiefertFalseFuerNull() {
        CustomerImpl customer = new CustomerImpl("Alice");
        assertFalse(customer.equals(null));
    }

    @Test
    public void equalsLiefertFalseFuerAndereKlasse() {
        CustomerImpl customer = new CustomerImpl("Alice");
        assertFalse(customer.equals("Alice"));
    }

    @Test
    public void equalsLiefertFalseFuerAnderenNamen() {
        CustomerImpl a = new CustomerImpl("Alice");
        CustomerImpl b = new CustomerImpl("Bob");
        assertFalse(a.equals(b));
    }

    @Test
    public void hashCodeIstGleichFuerGleichenNamen() {
        CustomerImpl a = new CustomerImpl("Alice");
        CustomerImpl b = new CustomerImpl("Alice");
        assertEquals(a.hashCode(), b.hashCode());
    }
}
