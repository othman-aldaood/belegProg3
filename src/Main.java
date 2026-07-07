import cli.ConsoleClient;
import domainLogic.WarehouseManager;
import events.PersistenceCommandListener;
import io.JBPPersistence;
import io.JOSPersistence;
import io.PersistenceStrategy;

import java.util.Scanner;

/**
 * Hauptklasse für den Start der Anwendung.
 * Liegt im default package, wie in den Anforderungen verlangt.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Argumente auslesen (Kapazität setzen, falls angegeben)
        int capacity = 100; // Standardkapazität
        if (args.length > 0) {
            try {
                capacity = Integer.parseInt(args[0]);
                System.out.println("Kapazität aus Argumenten gesetzt auf: " + capacity);
            } catch (NumberFormatException e) {
                // Falls das Argument keine Zahl ist (z.B. TCP/UDP später)
            }
        }

        // 2. Geschäftslogik initialisieren
        WarehouseManager gl = new WarehouseManager(capacity);

        // 3. CLI initialisieren und GL als Listener übergeben
        ConsoleClient cli = new ConsoleClient(gl);

        // 4. Observer und Feedback-Listener in der GL registrieren
        gl.setFeedbackListener(cli);
        gl.addCapacityObserver(cli);

        // 5. Persistenz einhängen (Prototyp 5)
        // Die UI kennt nur das Event-Interface, die Technologie-Auswahl passiert hier im setup.
        // Die Dateinamen kennt der DAL selbst (Folie 63, Java I/O).
        final WarehouseManager[] currentManager = {gl};
        cli.setPersistenceListener(new PersistenceCommandListener() {
            @Override
            public void onSave(String technology) {
                try {
                    createStrategy(technology).save(currentManager[0]);
                    System.out.println("Erfolg: Zustand gespeichert (" + technology + ").");
                } catch (Exception e) {
                    System.out.println("Fehler beim Speichern: " + e.getMessage());
                }
            }

            @Override
            public void onLoad(String technology) {
                try {
                    WarehouseManager loaded = createStrategy(technology).load();
                    currentManager[0] = loaded;
                    // Die Beobachter gehören nicht zum Zustand der GL und müssen laut
                    // Anforderung nach dem Laden nicht wieder eingehangen werden.
                    cli.setCommandListener(loaded);
                    System.out.println("Erfolg: Zustand geladen (" + technology + ").");
                } catch (Exception e) {
                    System.out.println("Fehler beim Laden: " + e.getMessage());
                }
            }
        });

        // 5. CLI starten
        System.out.println("=========================================================");
        System.out.println("       Willkommen in der Frachtverwaltung!               ");
        System.out.println("=========================================================");
        System.out.println("Verfügbare Hauptbefehle (Modi):");
        System.out.println("  :c  -> Einfügemodus (Kunde oder Frachtstück hinzufügen)");
        System.out.println("  :r  -> Anzeigemodus (customers, cargos, hazards lesen)");
        System.out.println("  :u  -> Änderungsmodus (Inspektionsdatum aktualisieren)");
        System.out.println("  :d  -> Löschmodus (Kunde oder Frachtstück entfernen)");
        System.out.println("  :p  -> Persistenzmodus (save/load [JOS|JBP])");
        System.out.println("  :x  -> Anwendung beenden");
        System.out.println("---------------------------------------------------------");
        System.out.println("Bitte einen Modus eingeben (z.B. ':c' gefolgt von Enter):");
        Scanner scanner = new Scanner(System.in);
        cli.start(scanner);
        scanner.close();
    }

    /**
     * Liefert die Persistenz-Strategie zur angegebenen Technologie.
     */
    private static PersistenceStrategy createStrategy(String technology) {
        if (technology.equals("JBP")) {
            return new JBPPersistence();
        }
        return new JOSPersistence();
    }
}