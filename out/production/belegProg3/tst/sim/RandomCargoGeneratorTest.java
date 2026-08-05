package sim;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unittests fuer den RandomCargoGenerator.
 * Die Zusicherungen gelten fuer jedes moegliche Zufallsergebnis und sind
 * damit deterministisch.
 */
class RandomCargoGeneratorTest {

    @Test
    void generateLiefertBekanntenFrachtTyp() {
        RandomCargoGenerator generator = new RandomCargoGenerator("Alice");
        CargoData data = generator.generate();
        assertTrue(Arrays.asList("DryBulkCargo", "UnitisedCargo").contains(data.type));
    }

    @Test
    void generateSetztDenKundinnenNamen() {
        RandomCargoGenerator generator = new RandomCargoGenerator("Alice");
        CargoData data = generator.generate();
        assertEquals("Alice", data.customerName);
    }

    @Test
    void generateLiefertWertVonMindestensHundert() {
        RandomCargoGenerator generator = new RandomCargoGenerator("Alice");
        CargoData data = generator.generate();
        assertTrue(data.value.compareTo(new BigDecimal(100)) >= 0);
    }

    @Test
    void generateLiefertGefahrenstoffSammlung() {
        RandomCargoGenerator generator = new RandomCargoGenerator("Alice");
        CargoData data = generator.generate();
        assertNotNull(data.hazards);
    }
}
