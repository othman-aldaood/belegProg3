package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * UDP-Server: jedes Datagramm ist eine eigenstaendige Anfrage, dadurch werden
 * mehrere Clients ueber denselben Socket bedient (gemeinsame Geschaeftslogik).
 *
 * Umgang mit den UDP-Eigenschaften (laut Anforderung zu beruecksichtigen):
 * - Paketverlust: der Client sendet nach Timeout erneut; damit ein wiederholtes
 *   Datagramm keinen Befehl doppelt ausfuehrt, merkt sich der Server pro Client
 *   die letzte Request-ID und liefert dafuer die gecachte Antwort.
 * - Reihenfolge: jede Anfrage traegt eine Request-ID, die in der Antwort
 *   zurueckgegeben wird; der Client ordnet Antworten darueber zu.
 */
public class UDPServer {

    private static final int MAX_PACKET_SIZE = 65507;

    private final int port;
    private final CommandProcessor processor;

    /** Letzte Request-ID und Antwort je Client (fuer wiederholte Datagramme). */
    private final Map<SocketAddress, String[]> lastResponses = new HashMap<>();

    public UDPServer(int port, CommandProcessor processor) {
        this.port = port;
        this.processor = processor;
    }

    /**
     * Startet den Server; blockiert dauerhaft.
     */
    public void start() throws IOException {
        try (DatagramSocket socket = new DatagramSocket(this.port)) {
            System.out.println("UDP-Server laeuft auf Port " + this.port);
            byte[] buffer = new byte[MAX_PACKET_SIZE];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String request = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                String response = handle(packet.getSocketAddress(), request);
                byte[] data = response.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(data, data.length, packet.getSocketAddress()));
            }
        }
    }

    /**
     * Verarbeitet eine Anfrage "requestId;Befehl" und liefert "requestId;Antwort".
     */
    private String handle(SocketAddress client, String request) {
        int separator = request.indexOf(';');
        if (separator <= 0) {
            return "0;Fehler: Ungueltige Anfrage.";
        }
        String requestId = request.substring(0, separator);
        String command = request.substring(separator + 1);

        // Wiederholtes Datagramm (Antwort ging verloren): gecachte Antwort liefern,
        // der Befehl darf nicht doppelt ausgefuehrt werden.
        String[] last = this.lastResponses.get(client);
        if (last != null && last[0].equals(requestId)) {
            return requestId + ";" + last[1];
        }

        String response = NetProtocol.encode(this.processor.process(command));
        this.lastResponses.put(client, new String[]{requestId, response});
        return requestId + ";" + response;
    }
}
