package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TCP-Client: haelt eine dauerhafte Verbindung zum Server und sendet pro
 * Befehl eine Zeile, auf die genau eine Antwort-Zeile folgt.
 */
public class TCPClient extends NetworkClient {

    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;

    public TCPClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    @Override
    protected synchronized String sendRequest(String line) {
        try {
            this.out.println(line);
            String response = this.in.readLine();
            return response == null ? "Fehler: Verbindung zum Server verloren." : response;
        } catch (IOException e) {
            return "Fehler: Keine Verbindung zum Server.";
        }
    }
}
