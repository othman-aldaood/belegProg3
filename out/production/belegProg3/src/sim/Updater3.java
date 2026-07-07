package sim;

import domainLogic.WarehouseManager;

import java.util.List;
import java.util.Random;

/**
 * Der neue Updater-Thread für die Simulation 3.
 * Wählt kontinuierlich ein zufälliges Frachtstück aus und aktualisiert dessen Inspektionsdatum.
 */
public class Updater3 implements Runnable {

    /**
     * Referenz auf die Geschäftslogik.
     */
    private final WarehouseManager gl;

    /**
     * Das gemeinsame Monitor-Objekt.
     */
    private final Object monitor;

    /**
     * Zufallsgenerator für die Auswahl.
     */
    private final Random random = new Random();

    /**
     * Konstruktor für den Updater-Thread.
     *
     * @param gl      Die Instanz der Geschäftslogik.
     * @param monitor Das Monitor-Objekt für wait/notify.
     */
    public Updater3(WarehouseManager gl, Object monitor) {
        this.gl = gl;
        this.monitor = monitor;
    }

    /**
     * Führt die Hauptschleife aus. Aktualisiert zufällige Frachtstücke.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (monitor) {
                while (gl.getStorageLocations().isEmpty()) {
                    monitor.notifyAll();
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                List<Integer> locations = gl.getStorageLocations();
                if (!locations.isEmpty()) {
                    int randomLoc = locations.get(random.nextInt(locations.size()));
                    gl.onUpdateInspectionDate(randomLoc);
                }

                monitor.notifyAll();
            }

            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}