package net;

import events.CargoCommandListener;
import events.GLFeedbackListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Verarbeitet eine textbasierte Anfrage des Clients und leitet sie als Event
 * an die Geschäftslogik weiter. Die Antwort der GL wird eingesammelt und als
 * Text zurückgegeben.
 *
 * Diese Klasse ist bewusst transportunabhängig (keine Sockets): TCP- und
 * UDP-Server verwenden denselben Processor, dadurch ist er ohne
 * Netzwerkzugriff testbar (Anforderung Testqualität).
 *
 * Protokoll: ein Befehl pro Zeile, Felder durch ';' getrennt, z.B.
 * INSERT_CARGO;DryBulkCargo;Alice;100.50;flammable,toxic;false;false;10
 * Ein einzelnes ',' im Gefahrenstoff-Feld bedeutet: keine Gefahrenstoffe.
 */
public class CommandProcessor implements GLFeedbackListener {

    private final CargoCommandListener listener;
    private final StringBuilder buffer = new StringBuilder();

    public CommandProcessor(CargoCommandListener listener) {
        this.listener = listener;
    }

    @Override
    public void onFeedbackReceived(String feedback) {
        this.buffer.append(feedback);
    }

    /**
     * Verarbeitet eine Anfrage-Zeile und liefert die Antwort der GL.
     * Synchronisiert, damit der Antwort-Puffer bei mehreren gleichzeitigen
     * Clients (Threads) konsistent bleibt.
     *
     * @param line die Anfrage des Clients
     * @return die Antwort für den Client (nie null)
     */
    public synchronized String process(String line) {
        this.buffer.setLength(0);
        try {
            String[] parts = line.split(";", -1);
            switch (parts[0]) {
                case "INSERT_CUSTOMER":
                    listener.onInsertCustomer(parts[1]);
                    break;
                case "INSERT_CARGO":
                    listener.onInsertCargo(parts[1], parts[2], new BigDecimal(parts[3]),
                            parseHazards(parts[4]), Boolean.parseBoolean(parts[5]),
                            Boolean.parseBoolean(parts[6]), Integer.parseInt(parts[7]));
                    break;
                case "READ_CUSTOMERS":
                    listener.onReadCustomers();
                    break;
                case "READ_CARGOS":
                    listener.onReadCargos(parts.length > 1 ? parts[1] : null);
                    break;
                case "READ_HAZARDS":
                    listener.onReadHazards(Boolean.parseBoolean(parts[1]));
                    break;
                case "UPDATE_INSPECTION":
                    listener.onUpdateInspectionDate(Integer.parseInt(parts[1]));
                    break;
                case "DELETE_CUSTOMER":
                    listener.onDeleteCustomer(parts[1]);
                    break;
                case "DELETE_CARGO":
                    listener.onDeleteCargo(Integer.parseInt(parts[1]));
                    break;
                default:
                    return "Fehler: Unbekannter Befehl.";
            }
        } catch (Exception e) {
            // Bedienfehler duerfen keine unkontrollierten Zustaende erzeugen
            return "Fehler: Ungueltige Anfrage.";
        }
        return this.buffer.length() == 0 ? "OK" : this.buffer.toString();
    }

    /**
     * Wandelt das Gefahrenstoff-Feld in eine Liste um; ',' bedeutet keine.
     */
    private List<String> parseHazards(String field) {
        List<String> hazards = new ArrayList<>();
        if (!field.equals(",")) {
            for (String h : field.split(",")) {
                if (!h.trim().isEmpty()) {
                    hazards.add(h.trim());
                }
            }
        }
        return hazards;
    }
}
