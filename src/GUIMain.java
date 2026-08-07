import domainLogic.WarehouseManager;
import events.PersistenceCommandListener;
import gui.WarehouseController;
import io.JBPPersistence;
import io.JOSPersistence;
import io.PersistenceStrategy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Haupteinstiegspunkt fuer die grafische Benutzeroberflaeche (GUI).
 * Liegt im default package, wie in den Anforderungen verlangt.
 */
public class GUIMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // FXML laden
        URL fxmlLocation = getClass().getResource("/gui/Warehouse.fxml");
        if (fxmlLocation == null) {
            throw new IllegalStateException("Warehouse.fxml konnte nicht gefunden werden.");
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();

        // Controller holen und GL uebergeben
        WarehouseController controller = loader.getController();

        // Kapazität optional per Kommandozeilenargument (wie im CLI)
        int capacity = 100;
        if (!getParameters().getRaw().isEmpty()) {
            try {
                capacity = Integer.parseInt(getParameters().getRaw().get(0));
            } catch (NumberFormatException ignored) {
            }
        }
        WarehouseManager manager = new WarehouseManager(capacity);

        // Rückmeldungen der GL werden im Status-Label der GUI angezeigt
        manager.setFeedbackListener(controller);
        controller.setWarehouseManager(manager);

        // Persistenz einhängen: die Oberfläche kennt nur das Event-Interface,
        // die Technologie-Auswahl wird hier im setup umgesetzt. Die Dateinamen
        // kennt der DAL selbst.
        final WarehouseManager[] currentManager = {manager};
        controller.setPersistenceListener(new PersistenceCommandListener() {
            @Override
            public void onSave(String technology) {
                try {
                    createStrategy(technology).save(currentManager[0]);
                    controller.onFeedbackReceived("Erfolg: Zustand gespeichert (" + technology + ").");
                } catch (Exception e) {
                    controller.onFeedbackReceived("Fehler beim Speichern: " + e.getMessage());
                }
            }

            @Override
            public void onLoad(String technology) {
                try {
                    WarehouseManager loaded = createStrategy(technology).load();
                    currentManager[0] = loaded;
                    // Der Feedback-Kanal wird neu verbunden, damit die
                    // Oberfläche weiterhin Rückmeldungen anzeigt.
                    loaded.setFeedbackListener(controller);
                    controller.setWarehouseManager(loaded);
                    controller.onFeedbackReceived("Erfolg: Zustand geladen (" + technology + ").");
                } catch (Exception e) {
                    controller.onFeedbackReceived("Fehler beim Laden: " + e.getMessage());
                }
            }
        });

        primaryStage.setTitle("Lagerverwaltung (Warehouse Management)");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    /**
     * Liefert die Persistenz-Strategie zur angegebenen Technologie.
     *
     * @param technology JOS oder JBP
     * @return die passende Strategie
     */
    private static PersistenceStrategy createStrategy(String technology) {
        if ("JBP".equals(technology)) {
            return new JBPPersistence();
        }
        return new JOSPersistence();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
