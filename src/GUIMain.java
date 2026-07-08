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
 * Muss sich im default package befinden!
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

        WarehouseManager manager = new WarehouseManager();
        controller.setWarehouseManager(manager);

        // Persistenz einhaengen (wie im CLI): Technologie-Auswahl und Dateinamen
        // kennt der DAL, die GUI sendet nur Events. Nach dem Laden wird die GL
        // ausgetauscht; Beobachter muessen laut Anforderung nicht wieder eingehangen werden.
        final WarehouseManager[] currentManager = {manager};
        controller.setPersistenceListener(new PersistenceCommandListener() {
            @Override
            public void onSave(String technology) {
                try {
                    createStrategy(technology).save(currentManager[0]);
                } catch (Exception e) {
                    System.out.println("Fehler beim Speichern: " + e.getMessage());
                }
            }

            @Override
            public void onLoad(String technology) {
                try {
                    WarehouseManager loaded = createStrategy(technology).load();
                    currentManager[0] = loaded;
                    controller.setWarehouseManager(loaded);
                } catch (Exception e) {
                    System.out.println("Fehler beim Laden: " + e.getMessage());
                }
            }
        });

        primaryStage.setTitle("Lagerverwaltung (Warehouse Management)");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Liefert die Persistenz-Strategie zur angegebenen Technologie.
     */
    private static PersistenceStrategy createStrategy(String technology) {
        if ("JBP".equals(technology)) {
            return new JBPPersistence();
        }
        return new JOSPersistence();
    }
}