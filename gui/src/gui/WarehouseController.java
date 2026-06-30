package gui;

import domainLogic.WarehouseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.time.Duration;
import java.util.Date;

public class WarehouseController {

    private WarehouseManager gl; // Referenz zur Geschaeftslogik

    @FXML
    private TableView<CustomerViewModel> kundenTabelle;
    @FXML
    private TableColumn<CustomerViewModel, String> kundenNameSpalte;
    @FXML
    private TableColumn<CustomerViewModel, Number> frachtAnzahlSpalte;

    @FXML
    private TableView<CargoViewModel> frachtTabelle;
    @FXML
    private TableColumn<CargoViewModel, Number> lagerplatzSpalte;
    @FXML
    private TableColumn<CargoViewModel, String> frachtKundeSpalte;
    @FXML
    private TableColumn<CargoViewModel, Date> inspektionsDatumSpalte;
    @FXML
    private TableColumn<CargoViewModel, Duration> einlagerungsDauerSpalte;

    // Eingabefelder
    @FXML
    private TextField kundenNameInput;
    @FXML
    private TextField frachtKundeInput;
    @FXML
    private TextField platzInput;

    // Listen fuer automatische Aktualisierung
    private final ObservableList<CustomerViewModel> kundenDaten = FXCollections.observableArrayList();
    private final ObservableList<CargoViewModel> frachtDaten = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Spalten binden
        kundenNameSpalte.setCellValueFactory(data -> data.getValue().nameProperty());
        frachtAnzahlSpalte.setCellValueFactory(data -> data.getValue().anzahlFrachtstueckeProperty());

        lagerplatzSpalte.setCellValueFactory(data -> data.getValue().lagerplatzProperty());
        frachtKundeSpalte.setCellValueFactory(data -> data.getValue().kundenNameProperty());
        inspektionsDatumSpalte.setCellValueFactory(data -> data.getValue().inspektionsDatumProperty());
        einlagerungsDauerSpalte.setCellValueFactory(data -> data.getValue().einlagerungsDauerProperty());

        kundenTabelle.setItems(kundenDaten);
        frachtTabelle.setItems(frachtDaten);

        setupDragAndDrop();
    }

    /**
     * Setzt die Geschaeftslogik (GL) und laedt die initialen Daten.
     */
    public void setWarehouseManager(WarehouseManager manager) {
        this.gl = manager;
        aktualisiereTabellen();
    }

    private void aktualisiereTabellen() {
        if (gl == null) return;

        kundenDaten.clear();
        frachtDaten.clear();

        // Kunden in GUI laden
        for (administration.Customer c : gl.getAllCustomers()) {
            kundenDaten.add(new CustomerViewModel(c.getName(), gl.getCargoCountForCustomer(c)));
        }

        // Frachtstücke in GUI laden
        java.util.Map<Integer, cargo.Cargo> cargosMap = gl.getCargosMap();
        java.util.Map<Integer, administration.Customer> ownersMap = gl.getCargoOwnersMap();
        java.util.Map<Integer, Date> inspectionMap = gl.getInspectionDatesMap();
        java.util.Map<Integer, Date> insertionMap = gl.getInsertionDatesMap();

        for (java.util.Map.Entry<Integer, cargo.Cargo> entry : cargosMap.entrySet()) {
            int platz = entry.getKey();
            String kunde = ownersMap.get(platz).getName();
            Date inspektion = inspectionMap.get(platz);
            Date insertion = insertionMap.get(platz);

            // Einlagerungsdauer berechnen
            long diffInMillies = Math.abs(new Date().getTime() - insertion.getTime());
            Duration dauer = Duration.ofMillis(diffInMillies);

            frachtDaten.add(new CargoViewModel(platz, kunde, inspektion, dauer));
        }
    }

    // --- Aktionen (CRUD) ---

    @FXML
    private void handleKundeAnlegen() {
        String name = kundenNameInput.getText();
        if (!name.isEmpty()) {
            gl.onInsertCustomer(name);
            aktualisiereTabellen();
            kundenNameInput.clear();
        }
    }

    @FXML
    private void handleFrachtEinfuegen() {
        String kunde = frachtKundeInput.getText();
        if (kunde.isEmpty()) return;

        // Nebenläufigkeit: Task läuft im Hintergrund!
        Task<Void> insertTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Fügen wir als Standard eine UnitisedCargo ein (für GUI Test)
                gl.onInsertCargo("UnitisedCargo", kunde, java.math.BigDecimal.TEN, java.util.Collections.emptyList(), false, false, 0);

                // Simuliere kleine Verzögerung um Nebenläufigkeit zu demonstrieren
                Thread.sleep(400);
                return null;
            }
        };

        insertTask.setOnSucceeded(e -> {
            aktualisiereTabellen();
            frachtKundeInput.clear();
        });

        Thread thread = new Thread(insertTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleFrachtLoeschen() {
        try {
            int platz = Integer.parseInt(platzInput.getText());
            gl.onDeleteCargo(platz);
            aktualisiereTabellen();
            platzInput.clear();
        } catch (NumberFormatException e) {
            System.out.println("Bitte gültige ID eingeben.");
        }
    }

    @FXML
    private void handleInspektionSetzen() {
        try {
            int platz = Integer.parseInt(platzInput.getText());
            gl.onUpdateInspectionDate(platz);
            aktualisiereTabellen();
        } catch (NumberFormatException e) {
            System.out.println("Bitte gültige ID eingeben.");
        }
    }

    // --- Drag & Drop Logik fuer Lagerplatztausch ---

    private void setupDragAndDrop() {
        frachtTabelle.setRowFactory(tv -> {
            TableRow<CargoViewModel> row = new TableRow<>();

            // 1. Drag starten
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(String.valueOf(row.getItem().lagerplatzProperty().get()));
                    db.setContent(cc);
                    event.consume();
                }
            });

            // 2. Drag ueber Ziel-Zeile
            row.setOnDragOver(event -> {
                if (event.getGestureSource() != row && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            // 3. Drop ausfuehren (Tauschen)
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString() && !row.isEmpty()) {
                    int sourcePlatz = Integer.parseInt(db.getString());
                    int targetPlatz = row.getItem().lagerplatzProperty().get();

                    if (sourcePlatz != targetPlatz) {
                        // Tausche die Plätze in der GL
                        boolean successSwap = gl.swapLagerplatz(sourcePlatz, targetPlatz);
                        if (successSwap) {
                            aktualisiereTabellen();
                            success = true;
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            });

            return row;
        });
    }
}