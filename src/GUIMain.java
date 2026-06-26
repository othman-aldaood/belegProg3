import domainLogic.WarehouseManager;
import gui.WarehouseController;
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

        // TODO: Initialisiere hier deine GL (WarehouseManager) so wie in der CLI
        WarehouseManager manager = new WarehouseManager(); // eventuell Parameter anpassen
        controller.setWarehouseManager(manager);

        primaryStage.setTitle("Lagerverwaltung (Warehouse Management)");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}