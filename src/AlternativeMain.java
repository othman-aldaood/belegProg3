import cli.ConsoleClient;
import domainLogic.WarehouseManager;
import events.CargoCommandListener;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Scanner;

/**
 * Alternativ konfiguriertes CLI.
 * Deaktivierte Funktionalitäten (laut Anforderung vorzugsweise):
 * Löschen von Kund*innen und Auflisten der Gefahrenstoffe.
 * Nur EIN Beobachter (Kapazitätswarnung) ist aktiv, wie gefordert.
 * Der Unterschied zum normalen CLI besteht nur im setup dieser main-Methode
 * (Einhängen der listener), nicht in der Implementierung.
 */
public class AlternativeMain {
    public static void main(String[] args) {
        int capacity = 100;
        if (args.length > 0) {
            try {
                capacity = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {}
        }

        WarehouseManager gl = new WarehouseManager(capacity);

        // Proxy-Pattern: Weiterleitung aller Befehle an die GL, AUSSER dem Löschen
        // von Kund*innen und dem Auflisten der Gefahrenstoffe (deaktiviert)
        CargoCommandListener restrictedListener = new CargoCommandListener() {
            @Override
            public void onInsertCustomer(String customerName) { gl.onInsertCustomer(customerName); }

            @Override
            public void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
                gl.onInsertCargo(type, customerName, value, hazards, isFragile, isPressurized, grainSize);
            }

            @Override
            public void onReadCustomers() { gl.onReadCustomers(); }

            @Override
            public void onReadCargos(String cargoType) { gl.onReadCargos(cargoType); }

            @Override
            public void onUpdateInspectionDate(int storageLocation) { gl.onUpdateInspectionDate(storageLocation); }

            @Override
            public void onDeleteCargo(int storageLocation) { gl.onDeleteCargo(storageLocation); }

            // Deaktivierte Funktionen
            @Override
            public void onDeleteCustomer(String customerName) {
                System.out.println("Löschen von Kund*innen ist in dieser alternativen Version DEAKTIVIERT.");
            }

            @Override
            public void onReadHazards(boolean existing) {
                System.out.println("Auflisten der Gefahrenstoffe ist in dieser alternativen Version DEAKTIVIERT.");
            }
        };

        ConsoleClient cli = new ConsoleClient(restrictedListener);

        // Nur ein Beobachter aktiv: die Kapazitätswarnung. Der Gefahrenstoff-
        // Beobachter entfällt, da sein pull das deaktivierte Auflisten der
        // Gefahrenstoffe verwenden würde.
        gl.setFeedbackListener(cli);
        gl.addCapacityObserver(cli);
        // gl.addHazardObserver(cli); // in dieser Konfiguration nicht registriert

        System.out.println("ALTERNATIVE Frachtverwaltung gestartet (Kapazität: " + capacity + ").");
        System.out.println("Deaktiviert: Löschen von Kund*innen, Auflisten der Gefahrenstoffe.");
        printBefehlsuebersicht();
        Scanner scanner = new Scanner(System.in);
        cli.start(scanner);
        scanner.close();
    }

    /**
     * Gibt einmalig eine Übersicht der verfügbaren Befehle aus.
     * Die deaktivierten Befehle sind gekennzeichnet.
     */
    private static void printBefehlsuebersicht() {
        System.out.println("Modi: :c einfuegen | :r anzeigen | :u aendern | :d loeschen | :x beenden");
        System.out.println(":c  [K-Name]");
        System.out.println(":c  [Fracht-Typ] [K-Name] [Wert] [Gefahrenstoffe] [[optionale Parameter]]");
        System.out.println("    Typen: DryBulkCargo [GrainSize] | UnitisedCargo [Fragile] | DryBulkAndUnitisedCargo [GrainSize] [Fragile]");
        System.out.println("    Gefahrenstoffe: explosive, flammable, toxic, radioactive (kommasepariert, einzelnes Komma fuer keine)");
        System.out.println(":r  customers | cargos [[Typ]] | hazards i bzw. hazards e (deaktiviert)");
        System.out.println(":u  [Lagerplatz]");
        System.out.println(":d  [Lagerplatz] | [K-Name] (deaktiviert)");
    }
}