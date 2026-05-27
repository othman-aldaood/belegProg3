package cli;

import events.CapacityObserver;
import events.CargoCommandListener;
import events.GLFeedbackListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Die Konsolen-basierte Benutzeroberfläche (CLI) für die Frachtverwaltung.
 * Implementiert die Beobachter- und Feedback-Schnittstellen strikt nach Belegvorgaben.
 * Keine Menüführung, zustandsbasiert.
 */
public class ConsoleClient implements CapacityObserver, GLFeedbackListener {
    private final CargoCommandListener listener;
    private String currentMode = "";

    public ConsoleClient(CargoCommandListener listener) {
        this.listener = listener;
    }

    public void start(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals(":x")) {
                break;
            }

            // Moduswechsel
            if (input.equals(":c") || input.equals(":r") || input.equals(":u") || input.equals(":d") || input.equals(":p")) {
                currentMode = input;
                continue;
            }

            // Eingabe im aktuellen Modus verarbeiten
            if (!currentMode.isEmpty() && !input.isEmpty()) {
                processInput(input);
            }
        }
    }

    private void processInput(String input) {
        try {
            switch (currentMode) {
                case ":c":
                    handleInsert(input);
                    break;
                case ":r":
                    handleRead(input);
                    break;
                case ":u":
                    listener.onUpdateInspectionDate(Integer.parseInt(input));
                    break;
                case ":d":
                    handleDelete(input);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Fehler: Ungültige Eingabe.");
        }
    }

    private void handleInsert(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 1) {
            // Kunde einfügen
            listener.onInsertCustomer(parts[0]);
        } else if (parts.length >= 3) {
            // Frachtstück einfügen
            String type = parts[0];
            String customer = parts[1];
            BigDecimal value = new BigDecimal(parts[2].replace(",", "."));

            List<String> hazards = new ArrayList<>();
            boolean isFragile = input.contains("fragile") || input.contains("true");
            boolean isPressurized = input.contains("pressurized");
            int grainSize = input.contains("grain") ? 10 : 0;

            for (int i = 3; i < parts.length; i++) {
                if (!parts[i].equals("fragile") && !parts[i].equals("pressurized") && !parts[i].equals("true") && !parts[i].equals("false") && !parts[i].matches("\\d+")) {
                    hazards.add(parts[i].replace(",", ""));
                }
            }
            listener.onInsertCargo(type, customer, value, hazards, isFragile, isPressurized, grainSize);
        }
    }

    private void handleRead(String input) {
        if (input.equals("customers")) {
            listener.onReadCustomers();
        } else if (input.startsWith("cargos")) {
            String[] parts = input.split(" ");
            listener.onReadCargos(parts.length > 1 ? parts[1] : null);
        } else if (input.startsWith("hazards")) {
            listener.onReadHazards(input.contains("i"));
        }
    }

    private void handleDelete(String input) {
        if (input.matches("\\d+")) {
            // Wenn es eine Zahl ist, ist es ein Lagerplatz
            listener.onDeleteCargo(Integer.parseInt(input));
        } else {
            // Sonst ist es ein Kundenname
            listener.onDeleteCustomer(input);
        }
    }

    @Override
    public void onCapacityWarning(String message) {
        System.out.println(message);
    }

    @Override
    public void onFeedbackReceived(String feedback) {
        System.out.println(feedback);
    }
}