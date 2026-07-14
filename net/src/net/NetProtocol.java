package net;

/**
 * Gemeinsame Konstanten und Hilfsfunktionen des textbasierten Protokolls.
 * Antworten der GL können mehrzeilig sein; da das Protokoll zeilenbasiert
 * arbeitet, werden Zeilenumbrueche fuer den Transport kodiert.
 */
public final class NetProtocol {

    /** Standard-Port fuer TCP- und UDP-Server. */
    public static final int PORT = 5555;

    private static final String NEWLINE_TOKEN = "<NL>";

    private NetProtocol() {
    }

    /**
     * Kodiert eine Antwort fuer den Transport (eine Zeile).
     */
    public static String encode(String response) {
        return response.replace("\n", NEWLINE_TOKEN);
    }

    /**
     * Dekodiert eine empfangene Antwort zurueck in Mehrzeilen-Text.
     */
    public static String decode(String response) {
        return response.replace(NEWLINE_TOKEN, "\n");
    }
}
