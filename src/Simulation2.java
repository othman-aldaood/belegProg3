import domainLogic.WarehouseManager;
import events.ChangeObserver;
import sim.CargoGenerator;
import sim.Konsument;
import sim.Produzent;
import sim.RandomCargoGenerator;

/**
 * Die Ausführungsklasse für Simulation 2.
 * Startet n Produzenten und n Konsumenten nebenläufig.
 * Die Konsolenausgabe der Änderungen erfolgt strikt über einen Beobachter (ChangeObserver).
 */
public class Simulation2 {

    /**
     * Der Haupteinstiegspunkt für Simulation 2.
     *
     * @param args Kommandozeilenargumente. args[0] = Kapazität, args[1] = Anzahl Threads (n).
     */
    public static void main(String[] args) {
        int capacity = 100;
        int n = 5;

        if (args.length >= 1) {
            try {
                capacity = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Kapazität. Nutze Standard: " + capacity);
            }
        }

        if (args.length >= 2) {
            try {
                n = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Ungültiges n. Nutze Standard: " + n);
            }
        }

        System.out.println("=== Starte Simulation 2 ===");
        System.out.println("Kapazität: " + capacity + " | Threads pro Typ: " + n);

        WarehouseManager gl = new WarehouseManager(capacity);

        // BEOBACHTER (Observer) REGISTRIEREN: Änderungen an der GL werden laut
        // Anforderung durch einen Beobachter auf der Konsole ausgegeben.
        // Der Beobachter erhält nur das Signal und fragt den Zustand selbst
        // bei der GL ab (pull).
        gl.addChangeObserver(new ChangeObserver() {
            /**
             * Gibt die Änderung synchronisiert für eine geordnete Konsolenausgabe aus.
             */
            @Override
            public void onChanged() {
                synchronized (System.out) {
                    System.out.println("[BEOBACHTER] Änderung an der GL, Bestand: "
                            + gl.getCurrentSize());
                }
            }
        });

        gl.onInsertCustomer("Alice");

        CargoGenerator randomGenerator = new RandomCargoGenerator("Alice");

        // Starten der n Produzenten und Konsumenten
        for (int i = 1; i <= n; i++) {
            Produzent produzent = new Produzent(gl, randomGenerator);
            Konsument konsument = new Konsument(gl);

            Thread prodThread = new Thread(produzent, "Produzent-" + i);
            Thread konsThread = new Thread(konsument, "Konsument-" + i);

            prodThread.start();
            konsThread.start();
        }

        // Endlos-Simulation, kein join() erforderlich.
    }
}