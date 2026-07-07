package sim;

/**
 * Schnittstelle (Interface) für die Generierung von Frachtstücken.
 * Sie ermöglicht die strikte Trennung von deterministischer Logik (für Tests)
 * und nicht-deterministischer Logik (für die echte Simulation) gemäß den Belegvorgaben.
 */
public interface CargoGenerator {

    /**
     * Generiert die Daten für ein neues Frachtstück.
     *
     * @return Ein {@link CargoData}-Objekt, das alle benötigten Parameter enthält.
     */
    CargoData generate();
}