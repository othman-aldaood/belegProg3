package io;

import domainLogic.WarehouseManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Schnittstelle für das Speichern und Laden der Geschäftslogik.
 * Abstrahiert die Datenzugriffsschicht (DAL) von der Geschäftslogik (GL).
 * Die stream-basierten Methoden ermöglichen Tests ohne Dateisystemzugriff.
 */
public interface PersistenceStrategy {

    /**
     * Liefert den Standard-Dateinamen dieser Technologie (ohne Pfad,
     * Speicherung im Ausführungsverzeichnis laut Anforderung).
     * Die Verbindungskonfiguration gehört zum DAL (Folie 63, Java I/O).
     *
     * @return der Dateiname
     */
    String getFilepath();

    /**
     * Schreibt den Zustand des WarehouseManagers in einen OutputStream.
     *
     * @param manager der zu speichernde Manager
     * @param out     der Ziel-Stream
     * @throws Exception falls ein Fehler beim Speichern auftritt
     */
    void save(WarehouseManager manager, OutputStream out) throws Exception;

    /**
     * Liest den Zustand eines WarehouseManagers aus einem InputStream.
     *
     * @param in der Quell-Stream
     * @return der geladene Manager
     * @throws Exception falls ein Fehler beim Laden auftritt
     */
    WarehouseManager load(InputStream in) throws Exception;

    /**
     * Speichert den aktuellen Zustand des WarehouseManagers in eine Datei.
     *
     * @param manager  der zu speichernde Manager
     * @param filepath der Dateipfad (ohne Verzeichnisse, laut Anforderung)
     * @throws Exception falls ein Fehler beim Speichern auftritt
     */
    default void save(WarehouseManager manager, String filepath) throws Exception {
        try (OutputStream out = new FileOutputStream(filepath)) {
            save(manager, out);
        }
    }

    /**
     * Speichert den Zustand in die Standard-Datei der Technologie.
     *
     * @param manager der zu speichernde Manager
     * @throws Exception falls ein Fehler beim Speichern auftritt
     */
    default void save(WarehouseManager manager) throws Exception {
        save(manager, getFilepath());
    }

    /**
     * Lädt den Zustand eines WarehouseManagers aus einer Datei.
     *
     * @param filepath der Dateipfad
     * @return der geladene Manager
     * @throws Exception falls ein Fehler beim Laden auftritt
     */
    default WarehouseManager load(String filepath) throws Exception {
        try (InputStream in = new FileInputStream(filepath)) {
            return load(in);
        }
    }

    /**
     * Lädt den Zustand aus der Standard-Datei der Technologie.
     *
     * @return der geladene Manager
     * @throws Exception falls ein Fehler beim Laden auftritt
     */
    default WarehouseManager load() throws Exception {
        return load(getFilepath());
    }
}
