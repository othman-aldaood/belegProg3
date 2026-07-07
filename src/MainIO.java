import domainLogic.WarehouseManager;
import io.PersistenceStrategy;
import io.JOSPersistence;
import io.JBPPersistence;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * Hauptklasse zur Demonstration der Persistierung (Prototyp 5).
 * Diese Klasse befindet sich im default package, wie in den Anforderungen verlangt.
 */
public class MainIO {

    public static void main(String[] args) {
        System.out.println("=== Prototyp 5: I/O Demonstration ===\n");

        // 1. Initialisierung und Befüllung der Geschäftslogik
        WarehouseManager originalManager = new WarehouseManager(10);

        // Kunden und Frachtstücke hinzufügen
        originalManager.onInsertCustomer("Alice");
        originalManager.onInsertCustomer("Bob");

        // Frachtstücke hinzufügen (mehrere, laut Anforderung)
        originalManager.onInsertCargo(
                "DryBulkCargo",
                "Alice",
                new BigDecimal("1500.50"),
                Collections.singletonList("explosive"),
                false,
                false,
                5
        );
        originalManager.onInsertCargo(
                "UnitisedCargo",
                "Bob",
                new BigDecimal("250.00"),
                Collections.singletonList("toxic"),
                true,
                false,
                0
        );

        System.out.println("--- Originaler Zustand ---");
        originalManager.onReadCustomers();
        originalManager.onReadCargos("");

        System.out.println("\n=========================================\n");

        // 2. Demonstration von JOS (Java Object Serialization)
        try {
            System.out.println("Starte JOS Speicherung...");
            PersistenceStrategy jos = new JOSPersistence();
            String josFilePath = "warehouse.ser";

            // Speichern
            jos.save(originalManager, josFilePath);
            System.out.println("JOS Speichern erfolgreich!");

            // Laden
            WarehouseManager loadedJosManager = jos.load(josFilePath);
            System.out.println("JOS Laden erfolgreich! Geladener Zustand:");
            loadedJosManager.onReadCustomers();
            loadedJosManager.onReadCargos("");

        } catch (Exception e) {
            System.err.println("Fehler bei JOS: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================\n");

        // 3. Demonstration von JBP (JavaBeans Persistence via XML)
        try {
            System.out.println("Starte JBP Speicherung...");
            PersistenceStrategy jbp = new JBPPersistence();
            String jbpFilePath = "warehouse.xml";

            // Speichern
            jbp.save(originalManager, jbpFilePath);
            System.out.println("JBP Speichern erfolgreich!");

            // Laden
            WarehouseManager loadedJbpManager = jbp.load(jbpFilePath);
            System.out.println("JBP Laden erfolgreich! Geladener Zustand:");
            loadedJbpManager.onReadCustomers();
            loadedJbpManager.onReadCargos("");

        } catch (Exception e) {
            System.err.println("Fehler bei JBP: " + e.getMessage());
            e.printStackTrace();
        }
    }
}