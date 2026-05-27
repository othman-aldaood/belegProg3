package events;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Schnittstelle für die Kommunikation von der Benutzeroberfläche (CLI) zur Geschäftslogik (GL).
 * Dient als einfaches Event-System, um Befehle ohne direkte Abhängigkeit weiterzuleiten.
 */
public interface CargoCommandListener {

    /**
     * Fügt eine neue Kundin oder einen neuen Kunden ein.
     * @param customerName Der Name der Kundin oder des Kunden.
     */
    void onInsertCustomer(String customerName);

    /**
     * Fügt ein neues Frachtstück ein.
     * @param type Der Typ des Frachtstücks (z.B. UnitisedCargo).
     * @param customerName Der Name der zugehörigen Kundin oder des Kunden.
     * @param value Der Wert des Frachtstücks.
     * @param hazards Eine Sammlung von Gefahrenstoffen.
     * @param isFragile Gibt an, ob das Frachtstück zerbrechlich ist.
     * @param isPressurized Gibt an, ob das Frachtstück unter Druck steht.
     * @param grainSize Die Korngroesse (nur relevant fuer Schuettgut).
     */
    void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize);

    /**
     * Fordert die Geschäftslogik auf, alle Kunden zu lesen und an die UI zu senden.
     */
    void onReadCustomers();

    /**
     * Fordert die Geschäftslogik auf, Frachtstücke eines bestimmten Typs zu lesen.
     * @param cargoType Der Typ der Frachtstücke (null für alle Typen).
     */
    void onReadCargos(String cargoType);

    /**
     * Fordert die Geschäftslogik auf, vorhandene oder nicht vorhandene Gefahrenstoffe zu lesen.
     *  @param existing true für vorhandene ('i'), false für nicht vorhandene ('e').
     */
    void onReadHazards(boolean existing);

    /**
     * Aktualisiert das Inspektionsdatum eines Frachtstücks auf das aktuelle Datum.
     * @param storageLocation Der Lagerplatz des Frachtstücks.
     */
    void onUpdateInspectionDate(int storageLocation);

    /**
     * Löscht eine Kundin oder einen Kunden.
     * @param customerName Der Name der zu löschenden Person.
     */
    void onDeleteCustomer(String customerName);

    /**
     * Entfernt ein Frachtstück aus dem Lager.
     * @param storageLocation Der Lagerplatz des zu entfernenden Frachtstücks.
     */
    void onDeleteCargo(int storageLocation);
}