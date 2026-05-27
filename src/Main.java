import cli.ConsoleClient;
import domainLogic.WarehouseManager;

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

        // 5. CLI starten
        System.out.println("=========================================================");
        System.out.println("       Willkommen in der Frachtverwaltung!               ");
        System.out.println("=========================================================");
        System.out.println("Verfügbare Hauptbefehle (Modi):");
        System.out.println("  :c  -> Einfügemodus (Kunde oder Frachtstück hinzufügen)");
        System.out.println("  :r  -> Anzeigemodus (customers, cargos, hazards lesen)");
        System.out.println("  :u  -> Änderungsmodus (Inspektionsdatum aktualisieren)");
        System.out.println("  :d  -> Löschmodus (Kunde oder Frachtstück entfernen)");
        System.out.println("  :x  -> Anwendung beenden");
        System.out.println("---------------------------------------------------------");
        System.out.println("Bitte einen Modus eingeben (z.B. ':c' gefolgt von Enter):");
        Scanner scanner = new Scanner(System.in);
        cli.start(scanner);
        scanner.close();
    }
}