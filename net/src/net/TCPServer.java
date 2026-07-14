package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP-Server: nimmt Verbindungen an und bearbeitet jeden Client in einem
 * eigenen Thread, dadurch werden mehrere konkurrierende Clients unterstuetzt.
 * Alle Clients teilen sich dieselbe Geschaeftslogik (ueber den gemeinsamen
 * CommandProcessor, der die Antworten synchronisiert erzeugt).
 */
public class TCPServer {

    private final int port;
    private final CommandProcessor processor;

    public TCPServer(int port, CommandProcessor processor) {
        this.port = port;
        this.processor = processor;
    }

    /**
     * Startet den Server; blockiert dauerhaft.
     */
    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("TCP-Server laeuft auf Port " + this.port);
            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    private void handleClient(Socket socket) {
        System.out.println("Client verbunden: " + socket.getRemoteSocketAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String line;
            while ((line = in.readLine()) != null) {
                out.println(NetProtocol.encode(this.processor.process(line)));
            }
        } catch (IOException e) {
            // Verbindungsabbruch eines Clients darf den Server nicht beeinflussen
        }
        System.out.println("Client getrennt: " + socket.getRemoteSocketAddress());
    }
}
