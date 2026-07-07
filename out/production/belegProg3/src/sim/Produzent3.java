package sim;

import domainLogic.WarehouseManager;

/**
 * Der modifizierte Produzent für die Simulation 3.
 * Diese Klasse implementiert das Einfügen von Frachtstücken unter strikter
 * Verwendung von wait/notify zur Synchronisation.
 * Optimiert: Nutzt getCurrentSize() anstelle von getStorageLocations().size()
 * zur Vermeidung von Memory Overhead.
 */
public class Produzent3 implements Runnable {

    /**
     * Referenz auf den WarehouseManager (Geschäftslogik).
     */
    private final WarehouseManager gl;

    /**
     * Der Generator zur Erstellung von Frachtdaten.
     */
    private final CargoGenerator generator;

    /**
     * Das gemeinsame Monitor-Objekt für die Synchronisation der Threads.
     */
    private final Object monitor;

    /**
     * Konstruktor für den Produzenten in Simulation 3.
     *
     * @param gl        Die Instanz der Geschäftslogik.
     * @param generator Der Generator für die Frachtstücke.
     * @param monitor   Das Monitor-Objekt für wait/notify.
     */
    public Produzent3(WarehouseManager gl, CargoGenerator generator, Object monitor) {
        this.gl = gl;
        this.generator = generator;
        this.monitor = monitor;
    }

    /**
     * Führt die Hauptschleife aus. Der Thread wartet, wenn die Kapazität
     * erreicht ist, und weckt andere Threads nach dem Einfügen auf.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // Generierung der Daten außerhalb des synchronized-Blocks
            // für bessere Performance und kürzere Sperrzeiten
            CargoData data = generator.generate();

            synchronized (monitor) {
                // Warten, solange das Lager voll ist (Speicheroptimiert)
                while (gl.getCurrentSize() >= gl.getCapacity()) {
                    monitor.notifyAll(); // Konsumenten wecken
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return; // Schleife bei Unterbrechung sicher verlassen
                    }
                }

                // Frachtstück einfügen
                gl.onInsertCargo(
                        data.type,
                        data.customerName,
                        data.value,
                        data.hazards,
                        data.isFragile,
                        data.isPressurized,
                        data.grainSize
                );

                monitor.notifyAll(); // Andere Threads benachrichtigen
            }


            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}