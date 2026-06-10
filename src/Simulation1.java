import domainLogic.WarehouseManager;
import sim.CargoGenerator;
import sim.Konsument;
import sim.Produzent;
import sim.RandomCargoGenerator;

/**
 * Die Ausführungsklasse für Simulation 1.
 * Startet einen Produzenten und einen Konsumenten gleichzeitig (nebenläufig).
 * Diese Simulation nimmt die Kapazität als Argument an und terminiert nicht.
 */
public class Simulation1 {

    /**
     * Der Haupteinstiegspunkt für Simulation 1.
     *
     * @param args Kommandozeilenargumente. args[0] definiert optional die Kapazität (Standard ist 100).
     */
    public static void main(String[] args) {
        int capacity = 100;
        if (args.length > 0) {
            try {
                capacity = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Kapazität in args. Nutze Standard: 100");
            }
        }

        System.out.println("=== Starte Simulation 1 (Kapazität: " + capacity + ") ===");

        WarehouseManager gl = new WarehouseManager(capacity);
        gl.onInsertCustomer("Alice");

        CargoGenerator randomGenerator = new RandomCargoGenerator("Alice");

        Produzent produzent = new Produzent(gl, randomGenerator);
        Konsument konsument = new Konsument(gl);

        Thread prodThread = new Thread(produzent, "Produzent-Thread");
        Thread konsThread = new Thread(konsument, "Konsument-Thread");

        prodThread.start();
        konsThread.start();

        // Keine .join() Aufrufe, da die Simulation endlos laufen soll.
    }
}