import domainLogic.WarehouseManager;
import net.CommandProcessor;
import net.NetProtocol;
import net.TCPServer;
import net.UDPServer;

/**
 * Startet den Server (Geschaeftslogik) fuer den Netzwerkbetrieb.
 * Argumente: [TCP|UDP] [Kapazitaet]
 * Liegt im default package, wie in den Anforderungen verlangt.
 */
public class ServerMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Verwendung: ServerMain [TCP|UDP] [Kapazitaet]");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Kapazitaet muss eine Zahl sein.");
            return;
        }

        // Geschaeftslogik lebt im Server-Prozess; die Beobachter muessen im
        // Netzwerkmodus laut Anforderung nicht unterstuetzt werden.
        WarehouseManager gl = new WarehouseManager(capacity);
        CommandProcessor processor = new CommandProcessor(gl);
        gl.setFeedbackListener(processor);

        if ("TCP".equals(args[0])) {
            new TCPServer(NetProtocol.PORT, processor).start();
        } else if ("UDP".equals(args[0])) {
            new UDPServer(NetProtocol.PORT, processor).start();
        } else {
            System.out.println("Fehler: Unbekanntes Protokoll '" + args[0] + "'.");
        }
    }
}
