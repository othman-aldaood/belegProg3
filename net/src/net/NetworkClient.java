package net;

import events.CargoCommandListener;
import events.GLFeedbackListener;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Basisklasse der Netzwerk-Clients: uebersetzt die Events der Benutzeroberflaeche
 * in Protokoll-Zeilen und leitet die Antwort des Servers als Feedback zurueck.
 * Der konkrete Transport (TCP oder UDP) wird von den Unterklassen realisiert.
 * Die Oberflaeche (ConsoleClient) bleibt dadurch unveraendert.
 */
public abstract class NetworkClient implements CargoCommandListener {

    private GLFeedbackListener feedbackListener;

    public void setFeedbackListener(GLFeedbackListener feedbackListener) {
        this.feedbackListener = feedbackListener;
    }

    /**
     * Sendet eine Anfrage-Zeile an den Server und liefert die Antwort-Zeile.
     */
    protected abstract String sendRequest(String line);

    private void send(String line) {
        String response = NetProtocol.decode(sendRequest(line));
        if (this.feedbackListener != null) {
            this.feedbackListener.onFeedbackReceived(response);
        } else {
            System.out.println(response);
        }
    }

    private String joinHazards(Collection<String> hazards) {
        if (hazards == null || hazards.isEmpty()) {
            return ",";
        }
        StringBuilder sb = new StringBuilder();
        for (String h : hazards) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(h);
        }
        return sb.toString();
    }

    @Override
    public void onInsertCustomer(String customerName) {
        send("INSERT_CUSTOMER;" + customerName);
    }

    @Override
    public void onInsertCargo(String type, String customerName, BigDecimal value, Collection<String> hazards, boolean isFragile, boolean isPressurized, int grainSize) {
        send("INSERT_CARGO;" + type + ";" + customerName + ";" + value + ";"
                + joinHazards(hazards) + ";" + isFragile + ";" + isPressurized + ";" + grainSize);
    }

    @Override
    public void onReadCustomers() {
        send("READ_CUSTOMERS");
    }

    @Override
    public void onReadCargos(String cargoType) {
        send("READ_CARGOS;" + (cargoType == null ? "" : cargoType));
    }

    @Override
    public void onReadHazards(boolean existing) {
        send("READ_HAZARDS;" + existing);
    }

    @Override
    public void onUpdateInspectionDate(int storageLocation) {
        send("UPDATE_INSPECTION;" + storageLocation);
    }

    @Override
    public void onDeleteCustomer(String customerName) {
        send("DELETE_CUSTOMER;" + customerName);
    }

    @Override
    public void onDeleteCargo(int storageLocation) {
        send("DELETE_CARGO;" + storageLocation);
    }
}
