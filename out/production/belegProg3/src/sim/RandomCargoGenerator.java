package sim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Nicht-deterministische Implementierung des {@link CargoGenerator}.
 * Diese Klasse erzeugt bei jedem Aufruf zufällige Frachtdaten und wird ausschließlich
 * in der echten Simulation (und NICHT in den Unit-Tests) verwendet.
 */
public class RandomCargoGenerator implements CargoGenerator {

    /**
     * Zufallsgenerator für die Werteerstellung.
     */
    private final Random random = new Random();

    /**
     * Verfügbare Frachttypen für die Simulation.
     */
    private final String[] types = {"DryBulkCargo", "UnitisedCargo"};

    /**
     * Der Name des Kunden, für den die Fracht generiert wird.
     */
    private final String customerName;

    /**
     * Konstruktor für den Zufallsgenerator.
     *
     * @param customerName Der Name des Kunden, der den generierten Frachtstücken zugewiesen wird.
     */
    public RandomCargoGenerator(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Generiert ein zufälliges Frachtstück basierend auf den definierten Typen und Eigenschaften.
     *
     * @return Ein vollständig initialisiertes {@link CargoData}-Objekt mit zufälligen Werten.
     */
    @Override
    public CargoData generate() {
        // 1. Zufällige Auswahl des Frachttyps
        String type = types[random.nextInt(types.length)];

        // 2. Generierung eines zufälligen Wertes zwischen 100 und 10100
        BigDecimal value = new BigDecimal(random.nextInt(10000) + 100);

        // 3. Zufällige Zuweisung von Gefahrenstoffen
        Collection<String> hazards = new ArrayList<>();
        if (random.nextBoolean()) hazards.add("explosive");
        if (random.nextBoolean()) hazards.add("toxic");

        // 4. Zufällige boolesche Eigenschaften
        boolean isFragile = random.nextBoolean();
        boolean isPressurized = random.nextBoolean();

        // 5. Korngröße wird nur bei Schüttgut (DryBulkCargo) generiert, andernfalls ist sie 0
        int grainSize = type.equals("DryBulkCargo") ? random.nextInt(50) + 1 : 0;

        return new CargoData(type, customerName, value, hazards, isFragile, isPressurized, grainSize);
    }
}