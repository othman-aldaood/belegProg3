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

            // --- Deaktivierte Funktionen (laut Anforderung dokumentiert) ---
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

        // NUR EIN BEOBACHTER AKTIV: nur die Kapazitätswarnung wird registriert.
        // Der Gefahrenstoff-Beobachter wird weggelassen (sein pull würde das
        // deaktivierte Auflisten der Gefahrenstoffe verwenden).
        gl.setFeedbackListener(cli);
        gl.addCapacityObserver(cli);
        // gl.addHazardObserver(cli); // <--- Abgeschaltet für die Alternative!

        System.out.println("ALTERNATIVE Frachtverwaltung gestartet"
                + " (deaktiviert: Löschen von Kund*innen, Auflisten der Gefahrenstoffe).");
        Scanner scanner = new Scanner(System.in);
        cli.start(scanner);
        scanner.close();
    }
}