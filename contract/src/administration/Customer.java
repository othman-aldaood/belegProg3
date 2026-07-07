package administration;

import java.io.Serializable;

/**
 * Repräsentiert einen Kunden im System.
 * Erweitert {@link Serializable} für die Java Object Serialization (JOS).
 */
public interface Customer extends Serializable {

    /**
     * Gibt den Namen des Kunden zurück.
     *
     * @return der Name des Kunden
     */
    String getName();

    /**
     * Setzt den Namen des Kunden.
     * Zwingend erforderlich für die JavaBeans Persistence (JBP) Konvention.
     *
     * @param name der neue Name des Kunden
     */
    void setName(String name);
}