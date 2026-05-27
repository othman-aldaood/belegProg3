import cli.ConsoleClient;
import domainLogic.WarehouseManager;
import events.CargoCommandListener;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Scanner;

/**
 * Alternativ konfiguriertes CLI.
 * Deaktivierte Funktionalitäten: Löschen von Kunden und Frachtstücken.
 * Nur EIN Beobachter (Feedback) ist aktiv, wie gefordert.
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

        // Proxy-Pattern: Weiterleitung aller Befehle an die GL, AUSSER den Lösch-Befehlen
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
            public void onReadHazards(boolean existing) { gl.onReadHazards(existing); }

            @Override
            public void onUpdateInspectionDate(int storageLocation) { gl.onUpdateInspectionDate(storageLocation); }

            // --- Deaktivierte Funktionen ---
            @Override
            public void onDeleteCustomer(String customerName) {
                System.out.println("Löschen von Kunden ist in dieser alternativen Version DEAKTIVIERT.");
            }

            @Override
            public void onDeleteCargo(int storageLocation) {
                System.out.println("Löschen von Frachtstücken ist in dieser alternativen Version DEAKTIVIERT.");
            }
        };

        ConsoleClient cli = new ConsoleClient(restrictedListener);

        // NUR EIN BEOBACHTER: Wir registrieren nur das Feedback, der CapacityObserver wird weggelassen!
        gl.setFeedbackListener(cli);
        // gl.addCapacityObserver(cli); // <--- Abgeschaltet für die Alternative!

        System.out.println("Willkommen in der ALTERNATIVEN Frachtverwaltung (Löschen & Kapazitätswarnung deaktiviert).");
        Scanner scanner = new Scanner(System.in);
        cli.start(scanner);
        scanner.close();
    }
}