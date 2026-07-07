package domainLogic;

import administration.Customer;

import java.util.Objects;

/**
 * Implementierung der Customer-Schnittstelle.
 * Kompatibel mit JOS und JBP (JavaBeans Konvention).
 */
public class CustomerImpl implements Customer {

    private static final long serialVersionUID = 1L;
    private String name;

    /**
     * Standardkonstruktor für JBP.
     */
    public CustomerImpl() {
    }

    /**
     * Konstruktor zur Initialisierung des Kunden.
     *
     * @param name der Name des Kunden
     */
    public CustomerImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerImpl customer = (CustomerImpl) o;
        return Objects.equals(name, customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}