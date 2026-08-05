package sim;

import domainLogic.WarehouseManager;

import java.util.List;
import java.util.Random;

/**
 * Der Updater-Thread für die Simulation 3.
 * Wählt kontinuierlich ein zufälliges Frachtstück aus und aktualisiert dessen
 * Inspektionsdatum. Er nimmt laut Anforderung NICHT an der
 * wait/notify-Synchronisation von Produzenten und Konsumenten teil.
 */
public class Updater3 implements Runnable {

    /**
     * Referenz auf die Geschäftslogik.
     */
    private final WarehouseManager gl;

    /**
     * Zufallsgenerator für die Auswahl.
     */
    private final Random random = new Random();

    /**
     * Konstruktor für den Updater-Thread.
     *
     * @param gl Die Instanz der Geschäftslogik.
     */
    public Updater3(WarehouseManager gl) {
        this.gl = gl;
    }

    /**
     * Führt die Hauptschleife aus. Aktualisiert zufällige Frachtstücke.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // Kritischer Bereich: Abrufen, Auswählen und Aktualisieren müssen
            // atomar erfolgen, damit kein anderer Thread dazwischen ändern kann.
            synchronized (gl) {
                List<Integer> locations = gl.getStorageLocations();
                if (!locations.isEmpty()) {
                    int randomLoc = locations.get(random.nextInt(locations.size()));
                    System.out.println(Thread.currentThread().getName()
                            + ": löst Inspektion auf Platz " + randomLoc + " aus");
                    gl.onUpdateInspectionDate(randomLoc);
                }
            }

            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
