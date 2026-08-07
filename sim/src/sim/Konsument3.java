package sim;

import domainLogic.WarehouseManager;

import java.util.Date;
import java.util.Map;

/**
 * Der modifizierte Konsument für die Simulation 3.
 * Diese Klasse löscht gezielt das Frachtstück mit dem ältesten Inspektionsdatum
 * und verwendet wait/notify zur Synchronisation.
 * Optimiert: Nutzt isEmpty() anstelle von getStorageLocations().isEmpty()
 * zur Vermeidung von Memory Overhead.
 */
public class Konsument3 implements Runnable {

    /**
     * Referenz auf die Geschäftslogik.
     */
    private final WarehouseManager gl;

    /**
     * Das gemeinsame Monitor-Objekt für wait/notify.
     */
    private final Object monitor;

    /**
     * Konstruktor für den Konsumenten in Simulation 3.
     *
     * @param gl      Die Instanz der Geschäftslogik.
     * @param monitor Das Monitor-Objekt.
     */
    public Konsument3(WarehouseManager gl, Object monitor) {
        this.gl = gl;
        this.monitor = monitor;
    }

    /**
     * Führt die Hauptschleife aus. Der Thread wartet, wenn das Lager leer ist,
     * sucht das älteste Frachtstück und löscht es.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (monitor) {
                // Warten, solange das Lager leer ist (Speicheroptimiert)
                while (gl.isEmpty()) {
                    monitor.notifyAll(); // Produzenten wecken
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return; // Schleife bei Unterbrechung sicher verlassen
                    }
                }

                // Ältestes Frachtstück suchen und löschen. Die Auswahl gehört
                // zur Simulation (nicht in die GL) und bildet zusammen mit dem
                // Löschen einen kritischen Bereich.
                synchronized (gl) {
                    Map<Integer, Date> inspections = gl.getInspectionDatesMap();
                    int oldestLoc = -1;
                    Date oldestDate = null;
                    for (Map.Entry<Integer, Date> entry : inspections.entrySet()) {
                        if (oldestDate == null || entry.getValue().before(oldestDate)) {
                            oldestDate = entry.getValue();
                            oldestLoc = entry.getKey();
                        }
                    }
                    if (oldestLoc != -1) {
                        System.out.println(Thread.currentThread().getName()
                                + ": lösche ältestes Frachtstück auf Platz " + oldestLoc);
                        gl.onDeleteCargo(oldestLoc);
                    }
                }

                monitor.notifyAll(); // Produzenten benachrichtigen
            }

            // Für die Abgabe ist Thread.sleep auf 0 gesetzt.
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}