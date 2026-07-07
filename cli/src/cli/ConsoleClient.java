package cli;

import events.CapacityObserver;
import events.CargoCommandListener;
import events.GLFeedbackListener;
import events.PersistenceCommandListener;

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
    private CargoCommandListener listener;
    private PersistenceCommandListener persistenceListener;
    private String currentMode = "";

    public ConsoleClient(CargoCommandListener listener) {
        this.listener = listener;
    }

    /**
     * Setzt den Handler fuer den Persistenzmodus (:p).
     * Wird im setup (main) eingehangen; ohne Handler ist der Modus deaktiviert.
     */
    public void setPersistenceListener(PersistenceCommandListener persistenceListener) {
        this.persistenceListener = persistenceListener;
    }

    /**
     * Tauscht die Geschäftslogik aus, z.B. nach dem Laden eines gespeicherten Zustands.
     */
    public void setCommandListener(CargoCommandListener listener) {
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
                case ":p":
                    handlePersistence(input);
                    break;
            }
        } catch (Exception e) {
            System.out.println("Fehler: Ungültige Eingabe.");
        }
    }

    /**
     * Einfügemodus laut Anforderung:
     * [K-Name] fügt eine Kundin ein.
     * [Fracht-Typ] [K-Name] [Wert] [kommaseparierte Gefahrenstoffe, einzelnes Komma
     * für keine] [[optionale Parameter]] fügt ein Frachtstück ein. Die Reihenfolge
     * der optionalen Parameter wird durch den Typ-Namen bestimmt (DryBulk -> grainSize,
     * Unitised -> fragile); bei keiner Angabe gelten default-Werte.
     */
    private void handleInsert(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 1) {
            // Kunde einfügen
            listener.onInsertCustomer(parts[0]);
            return;
        }
        if (parts.length < 4) {
            System.out.println("Fehler: Ungueltige Eingabe.");
            return;
        }

        String type = parts[0];
        String customer = parts[1];
        BigDecimal value = new BigDecimal(parts[2].replace(",", "."));

        // Gefahrenstoffe: einzelnes Komma bedeutet keine
        List<String> hazards = new ArrayList<>();
        if (!parts[3].equals(",")) {
            for (String h : parts[3].split(",")) {
                if (!h.trim().isEmpty()) {
                    hazards.add(h.trim());
                }
            }
        }

        // Optionale Parameter in der Reihenfolge des Typ-Namens, defaults bei keiner Angabe
        int grainSize = 0;
        boolean isFragile = false;
        int index = 4;
        if (type.contains("DryBulk") && index < parts.length) {
            grainSize = Integer.parseInt(parts[index]);
            index++;
        }
        if (type.contains("Unitised") && index < parts.length) {
            isFragile = Boolean.parseBoolean(parts[index]);
        }

        listener.onInsertCargo(type, customer, value, hazards, isFragile, false, grainSize);
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

    /**
     * Verarbeitet Eingaben im Persistenzmodus (:p).
     * Befehle: save [JOS|JBP] bzw. load [JOS|JBP]
     */
    private void handlePersistence(String input) {
        if (this.persistenceListener == null) {
            System.out.println("Fehler: Persistenz ist nicht verfuegbar.");
            return;
        }
        String[] parts = input.split(" ");
        if (parts.length != 2 || (!parts[1].equals("JOS") && !parts[1].equals("JBP"))) {
            System.out.println("Fehler: Ungueltige Eingabe. Erwartet: save [JOS|JBP] oder load [JOS|JBP]");
            return;
        }
        if (parts[0].equals("save")) {
            this.persistenceListener.onSave(parts[1]);
        } else if (parts[0].equals("load")) {
            this.persistenceListener.onLoad(parts[1]);
        } else {
            System.out.println("Fehler: Ungueltige Eingabe. Erwartet: save [JOS|JBP] oder load [JOS|JBP]");
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