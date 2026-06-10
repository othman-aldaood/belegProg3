package sim;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Ein Datentransferobjekt (DTO), das die generierten Frachtdaten deterministisch kapselt.
 * Diese Klasse dient als reiner Datencontainer zur Übertragung der Werte zwischen
 * dem Generator und dem Produzenten, um die Trennung von Logik und Daten zu gewährleisten.
 */
public class CargoData {
    public final String type;
    public final String customerName;
    public final BigDecimal value;
    public final Collection<String> hazards;
    public final boolean isFragile;
    public final boolean isPressurized;
    public final int grainSize;

    /**
     * Konstruktor zur Initialisierung aller unveränderlichen (finalen) Felder.
     *
     * @param type          Der Typ des Frachtstücks (z.B. "DryBulkCargo" oder "UnitisedCargo").
     * @param customerName  Der Name des Kunden, dem die Fracht gehört.
     * @param value         Der monetäre Wert der Fracht.
     * @param hazards       Eine Sammlung von Gefahrenstoffen (z.B. "explosive", "toxic").
     * @param isFragile     Gibt an, ob die Fracht zerbrechlich ist (nur für UnitisedCargo relevant).
     * @param isPressurized Gibt an, ob die Fracht unter Druck steht (nur für UnitisedCargo relevant).
     * @param grainSize     Die Korngröße des Schüttguts (nur für DryBulkCargo relevant).
     */
    public CargoData(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
        this.type = type;
        this.customerName = customerName;
        this.value = value;
        this.hazards = hazards;
        this.isFragile = isFragile;
        this.isPressurized = isPressurized;
        this.grainSize = grainSize;
    }
}