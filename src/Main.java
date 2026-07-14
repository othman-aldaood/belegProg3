import cli.ConsoleClient;
import domainLogic.WarehouseManager;
import events.PersistenceCommandListener;
import io.JBPPersistence;
import io.JOSPersistence;
import io.PersistenceStrategy;

import net.NetProtocol;
import net.NetworkClient;
import net.TCPClient;
import net.UDPClient;

import java.util.Scanner;

/**
 * Hauptklasse für den Start der Anwendung.
 * Liegt im default package, wie in den Anforderungen verlangt.
 * Argumente: eine Zahl setzt die Kapazität; TCP oder UDP startet die
 * Anwendung als Client für das entsprechende Protokoll (der Server läuft
 * bereits und an ihm wurde die Kapazität gesetzt).
 */
public class Main {
    public static void main(String[] args) {
        // 1. Argumente auslesen
        int capacity = 100; // Standardkapazität
        if (args.length > 0) {
            if (args[0].equals("TCP") || args[0].equals("UDP")) {
                startNetworkClient(args[0]);
                return;
            }
            try {
                capacity = Integer.parseInt(args[0]);
                System.out.println("Kapazität aus Argumenten gesetzt auf: " + capacity);
            } catch (NumberFormatException e) {
                // ungültiges Argument -> Standardkapazität
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
     * Startet das CLI als Netzwerk-Client (Prototyp 6).
     * Die Oberfläche bleibt unverändert, nur der Listener wird durch den
     * Netzwerk-Client ersetzt (Konfiguration im setup). Beobachter und
     * Persistenz müssen im Netzwerkmodus laut Anforderung nicht unterstützt werden.
     */
    private static void startNetworkClient(String protocol) {
        NetworkClient netClient;
        try {
            if (protocol.equals("TCP")) {
                netClient = new TCPClient("localhost", NetProtocol.PORT);
            } else {
                netClient = new UDPClient("localhost", NetProtocol.PORT);
            }
        } catch (Exception e) {
            System.out.println("Fehler: Server nicht erreichbar (" + e.getMessage() + ").");
            return;
        }

        ConsoleClient cli = new ConsoleClient(netClient);
        netClient.setFeedbackListener(cli);

        System.out.println("Frachtverwaltung als " + protocol + "-Client gestartet (:x beendet).");
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