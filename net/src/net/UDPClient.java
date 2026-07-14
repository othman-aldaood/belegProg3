package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * UDP-Client: sendet pro Befehl ein Datagramm "requestId;Befehl" und wartet
 * auf die Antwort "requestId;Antwort".
 *
 * Umgang mit den UDP-Eigenschaften (laut Anforderung zu beruecksichtigen):
 * - Paketverlust: nach einem Timeout wird die Anfrage erneut gesendet
 *   (begrenzte Anzahl Versuche).
 * - Reihenfolge: Antworten mit einer fremden (veralteten) Request-ID werden
 *   verworfen; nur die Antwort zur aktuellen Anfrage wird akzeptiert.
 */
public class UDPClient extends NetworkClient {

    private static final int MAX_PACKET_SIZE = 65507;
    private static final int TIMEOUT_MS = 1000;
    private static final int MAX_ATTEMPTS = 3;

    private final DatagramSocket socket;
    private final InetSocketAddress serverAddress;
    private long requestId = 0;

    public UDPClient(String host, int port) throws IOException {
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(TIMEOUT_MS);
        this.serverAddress = new InetSocketAddress(host, port);
    }

    @Override
    protected synchronized String sendRequest(String line) {
        this.requestId++;
        String expectedId = String.valueOf(this.requestId);
        byte[] data = (expectedId + ";" + line).getBytes(StandardCharsets.UTF_8);

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                this.socket.send(new DatagramPacket(data, data.length, this.serverAddress));
                while (true) {
                    byte[] buffer = new byte[MAX_PACKET_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    this.socket.receive(packet);
                    String response = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                    int separator = response.indexOf(';');
                    if (separator > 0 && response.substring(0, separator).equals(expectedId)) {
                        return response.substring(separator + 1);
                    }
                    // veraltete oder umsortierte Antwort -> verwerfen und weiter warten
                }
            } catch (SocketTimeoutException e) {
                // Paketverlust vermutet -> erneut senden
            } catch (IOException e) {
                return "Fehler: Keine Verbindung zum Server.";
            }
        }
        return "Fehler: Server antwortet nicht (UDP).";
    }
}
