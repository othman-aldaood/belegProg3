package sim;

import domainLogic.WarehouseManager;

import java.util.List;
import java.util.Random;

/**
 * Der Konsument (Thread) für Simulation 1 und 2.
 * Löscht kontinuierlich zufällige Frachtstücke aus dem Lager.
 * Terminiert nicht regulär, sondern läuft bis zur Unterbrechung.
 */
public class Konsument implements Runnable {

    /**
     * Referenz auf den WarehouseManager, um die belegten Plätze abzufragen.
     */
    private final WarehouseManager gl;

    /**
     * Zufallsgenerator für die Auswahl des Lagerplatzes.
     */
    private final Random random = new Random();

    /**
     * Konstruktor für den Konsumenten.
     *
     * @param gl Die Instanz des WarehouseManagers.
     */
    public Konsument(WarehouseManager gl) {
        this.gl = gl;
    }

    /**
     * Führt die Hauptschleife des Konsumenten aus.
     * Ruft die Liste der belegten Plätze ab, wählt zufällig einen aus und löscht ihn.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // 1. Enthaltene Frachtstücke (Lagerplätze) abrufen
            List<Integer> locations = gl.getStorageLocations();

            // 2. Daraus zufällig eines auswählen und löschen (falls nicht leer)
            if (!locations.isEmpty()) {
                int randomLocation = locations.get(random.nextInt(locations.size()));
                gl.onDeleteCargo(randomLocation);
            }

            // Für die Abgabe MUSS Thread.sleep auf 0 gesetzt sein!
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}