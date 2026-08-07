import cli.ConsoleClient;
import domainLogic.WarehouseManager;
import events.CargoCommandListener;
import events.PersistenceCommandListener;
import io.JBPPersistence;
import io.JOSPersistence;
import io.PersistenceStrategy;
import log.LogTexts;
import log.LogWriter;
import log.LoggingChangeObserver;
import log.LoggingCommandListener;

import net.NetProtocol;
import net.NetworkClient;
import net.TCPClient;
import net.UDPClient;

import java.io.IOException;
import java.util.Scanner;

/**
 * Hauptklasse für den Start der Anwendung.
 * Liegt im default package, wie in den Anforderungen verlangt.
 * Argumente: eine Zahl setzt die Kapazität; TCP oder UDP startet die
 * Anwendung als Client für das entsprechende Protokoll (der Server läuft
 * bereits und an ihm wurde die Kapazität gesetzt); DE oder EN aktiviert
 * zusätzlich das Log in der entsprechenden Sprache.
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
            } catch (NumberFormatException e) {
                // ungültiges Argument -> Standardkapazität
            }
        }

        // Sprachkürzel für das optionale Log ermitteln (ohne Angabe kein Log)
        String sprache = null;
        for (String argument : args) {
            if (argument.equals("DE") || argument.equals("EN")) {
                sprache = argument;
            }
        }

        // 2. Geschäftslogik initialisieren
        WarehouseManager gl = new WarehouseManager(capacity);

        // 3. Log optional einhängen: der protokollierende Stellvertreter wird
        // zwischen Oberfläche und Geschäftslogik gesetzt, der Beobachter
        // protokolliert die Zustandsänderungen.
        LogWriter logWriter = null;
        LogTexts logTexte = null;
        if (sprache != null) {
            try {
                logWriter = LogWriter.forLogFile();
                logTexte = new LogTexts(sprache);
                gl.addChangeObserver(new LoggingChangeObserver(logWriter, logTexte));
                System.out.println("Log aktiviert (" + sprache + "): " + LogWriter.LOG_FILENAME);
            } catch (IOException e) {
                logWriter = null;
                System.out.println("Fehler: Log konnte nicht geöffnet werden (" + e.getMessage() + ").");
            }
        }
        final LogWriter aktivesLog = logWriter;
        final LogTexts aktiveLogTexte = logTexte;

        // 4. CLI initialisieren und den Listener übergeben
        ConsoleClient cli = new ConsoleClient(protokolliere(gl, aktivesLog, aktiveLogTexte));

        // 5. Observer und Feedback-Listener in der GL registrieren
        // (beide Beobachter: Kapazitätswarnung ab 90% und Gefahrenstoff-Änderungen)
        gl.setFeedbackListener(cli);
        gl.addCapacityObserver(cli);
        gl.addHazardObserver(cli);

        // 6. Persistenz einhängen (Prototyp 5)
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
                    // Die Beobachter gehören nicht zum Zustand der GL und müssen
                    // laut Anforderung nach dem Laden nicht wieder eingehangen
                    // werden; sie werden hier im setup erneut registriert.
                    loaded.setFeedbackListener(cli);
                    loaded.addCapacityObserver(cli);
                    loaded.addHazardObserver(cli);
                    // Das Log wird auch an der geladenen Geschäftslogik wieder
                    // eingehangen, damit weiterhin protokolliert wird.
                    if (aktivesLog != null) {
                        loaded.addChangeObserver(new LoggingChangeObserver(aktivesLog, aktiveLogTexte));
                    }
                    cli.setCommandListener(protokolliere(loaded, aktivesLog, aktiveLogTexte));
                    System.out.println("Erfolg: Zustand geladen (" + technology + ").");
                } catch (Exception e) {
                    System.out.println("Fehler beim Laden: " + e.getMessage());
                }
            }
        });

        // 7. CLI starten. Die Befehlsübersicht wird einmalig ausgegeben.
        System.out.println("Frachtverwaltung gestartet (Kapazität: " + capacity + ").");
        printBefehlsuebersicht();
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
     * Gibt einmalig eine Übersicht der verfügbaren Befehle aus.
     */
    private static void printBefehlsuebersicht() {
        System.out.println("Modi: :c einfuegen | :r anzeigen | :u aendern | :d loeschen | :p persistenz | :x beenden");
        System.out.println(":c  [K-Name]");
        System.out.println(":c  [Fracht-Typ] [K-Name] [Wert] [Gefahrenstoffe] [[optionale Parameter]]");
        System.out.println("    Typen: DryBulkCargo [GrainSize] | UnitisedCargo [Fragile] | DryBulkAndUnitisedCargo [GrainSize] [Fragile]");
        System.out.println("    Gefahrenstoffe: explosive, flammable, toxic, radioactive (kommasepariert, einzelnes Komma fuer keine)");
        System.out.println("    Beispiel: DryBulkAndUnitisedCargo Alice 4004,50 flammable,toxic 10 true");
        System.out.println(":r  customers | cargos [[Typ]] | hazards i | hazards e");
        System.out.println(":u  [Lagerplatz]");
        System.out.println(":d  [K-Name] | [Lagerplatz]");
        System.out.println(":p  save [JOS|JBP] | load [JOS|JBP]");
    }

    /**
     * Liefert den Listener, über den die Oberfläche die Geschäftslogik erreicht.
     * Ist das Log aktiv, wird der protokollierende Stellvertreter davorgesetzt,
     * andernfalls wird die Geschäftslogik direkt verwendet.
     *
     * @param gl     die Geschäftslogik
     * @param writer die Ausgabe des Logs oder null, wenn das Log inaktiv ist
     * @param texte  die Textquelle des Logs oder null, wenn das Log inaktiv ist
     * @return der zu verwendende Listener
     */
    private static CargoCommandListener protokolliere(WarehouseManager gl, LogWriter writer, LogTexts texte) {
        if (writer == null) {
            return gl;
        }
        return new LoggingCommandListener(gl, writer, texte);
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