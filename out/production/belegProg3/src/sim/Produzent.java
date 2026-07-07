package sim;

import events.CargoCommandListener;

/**
 * Der Produzent (Thread) für Simulation 1 und 2.
 * Generiert kontinuierlich Frachtstücke und fügt sie in das Lager ein.
 * Gemäß den Vorgaben terminiert dieser Thread nicht regulär,
 * sondern läuft in einer Endlosschleife, bis er unterbrochen (interrupted) wird.
 */
public class Produzent implements Runnable {

    /**
     * Die Geschäftslogik (WarehouseManager) zum Einfügen der Frachtstücke.
     */
    private final CargoCommandListener gl;

    /**
     * Der Generator zur deterministischen oder zufälligen Erstellung von Frachtdaten.
     */
    private final CargoGenerator generator;

    /**
     * Konstruktor für den Produzenten.
     *
     * @param gl        Die Schnittstelle zur Geschäftslogik.
     * @param generator Der Generator zur Erstellung von Frachtdaten.
     */
    public Produzent(CargoCommandListener gl, CargoGenerator generator) {
        this.gl = gl;
        this.generator = generator;
    }

    /**
     * Führt die Hauptschleife des Produzenten aus.
     * Generiert Frachtstücke und fügt sie kontinuierlich ein.
     */
    @Override
    public void run() {
        // Unendliche Schleife, bis der Thread unterbrochen wird
        while (!Thread.currentThread().isInterrupted()) {
            CargoData data = generator.generate();

            gl.onInsertCargo(
                    data.type,
                    data.customerName,
                    data.value,
                    data.hazards,
                    data.isFragile,
                    data.isPressurized,
                    data.grainSize
            );


            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Schleife beenden
            }
        }
    }
}