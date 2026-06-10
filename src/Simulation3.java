import domainLogic.WarehouseManager;
import sim.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Die Ausführungsklasse für Simulation 3.
 * Erfüllt alle Vorgaben bezüglich wait/notify, ScheduledExecutorService
 * und der Parameterübergabe per Kommandozeile.
 */
public class Simulation3 {

    /**
     * Der Haupteinstiegspunkt für Simulation 3.
     *
     * @param args Kommandozeilenargumente:
     *             args[0] = Kapazität
     *             args[1] = Anzahl der Threads (Einfügen, Löschen, Update)
     *             args[2] = Intervall in Millisekunden
     */
    public static void main(String[] args) {
        int capacity = 100;
        int numThreads = 5;
        long intervalMs = 1000;

        // 1. Argumente auslesen
        if (args.length >= 1) {
            try {
                capacity = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }
        if (args.length >= 2) {
            try {
                numThreads = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        if (args.length >= 3) {
            try {
                intervalMs = Long.parseLong(args[2]);
            } catch (NumberFormatException ignored) {
            }
        }

        System.out.println("=== Starte Simulation 3 ===");
        System.out.println("Kapazität: " + capacity + " | Threads: " + numThreads + " | Intervall: " + intervalMs + "ms");

        WarehouseManager gl = new WarehouseManager(capacity);

        // 2. BEOBACHTER DEAKTIVIEREN (Stummgeschaltet)
        gl.setFeedbackListener(feedback -> { /* Keine Ausgabe auf der Konsole */ });

        gl.onInsertCustomer("Alice");

        CargoGenerator randomGenerator = new RandomCargoGenerator("Alice");
        Object monitor = new Object(); // Gemeinsamer Monitor für wait/notify

        // 3. SCHEDULED EXECUTOR SERVICE (gibt den Zustand periodisch aus)
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            int currentSize = gl.getStorageLocations().size();
            System.out.println("[STATUS-UPDATE] Aktuelle Auslastung: " + currentSize + " / " + gl.getCapacity());
        }, 0, intervalMs, TimeUnit.MILLISECONDS);

        // 4. THREADS STARTEN
        for (int i = 1; i <= numThreads; i++) {
            new Thread(new Produzent3(gl, randomGenerator, monitor), "Produzent-" + i).start();
            new Thread(new Konsument3(gl, monitor), "Konsument-" + i).start();
            new Thread(new Updater3(gl, monitor), "Updater-" + i).start();
        }
    }
}