package gui;

import domainLogic.WarehouseManager;
import events.GLFeedbackListener;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller der grafischen Oberfläche. Bietet den gleichen Funktionsumfang
 * wie das CLI (abzüglich der Beobachter und der Netzwerkfunktionalität):
 * Kund*innen anlegen/löschen, Frachtstücke aller Typen einfügen (mit Wert,
 * Gefahrenstoffen und optionalen Parametern), Anzeigen mit Typ-Filter,
 * Gefahrenstoffe (vorhanden/nicht vorhanden), Inspektion und Löschen.
 * Rückmeldungen der GL werden über das Feedback-Event im Status-Label
 * angezeigt (explizites data binding).
 */
public class WarehouseController implements GLFeedbackListener {

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
    private TableColumn<CargoViewModel, Number> einlagerungsDauerSpalte;

    // Eingabefelder
    @FXML
    private TextField kundenNameInput;
    @FXML
    private ComboBox<String> typAuswahl;
    @FXML
    private TextField frachtKundeInput;
    @FXML
    private TextField wertInput;
    @FXML
    private TextField hazardsInput;
    @FXML
    private TextField grainSizeInput;
    @FXML
    private CheckBox fragileInput;
    @FXML
    private TextField platzInput;
    @FXML
    private TextField typFilterInput;
    @FXML
    private Label statusLabel;

    // Listen fuer automatische Aktualisierung
    private final ObservableList<CustomerViewModel> kundenDaten = FXCollections.observableArrayList();
    private final ObservableList<CargoViewModel> frachtDaten = FXCollections.observableArrayList();

    // Statusmeldung (explizites data binding an das Status-Label)
    private final StringProperty statusText = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        // Spalten binden (data binding ueber die JavaFX-Properties der ViewModels)
        kundenNameSpalte.setCellValueFactory(data -> data.getValue().nameProperty());
        frachtAnzahlSpalte.setCellValueFactory(data -> data.getValue().anzahlFrachtstueckeProperty());

        lagerplatzSpalte.setCellValueFactory(data -> data.getValue().lagerplatzProperty());
        frachtKundeSpalte.setCellValueFactory(data -> data.getValue().kundenNameProperty());
        inspektionsDatumSpalte.setCellValueFactory(data -> data.getValue().inspektionsDatumProperty());
        einlagerungsDauerSpalte.setCellValueFactory(data -> data.getValue().einlagerungsDauerProperty());

        kundenTabelle.setItems(kundenDaten);
        frachtTabelle.setItems(frachtDaten);

        // Unterstuetzt werden alle Typen, die von Cargo und Storable ableiten
        typAuswahl.setItems(FXCollections.observableArrayList(
                "DryBulkCargo", "UnitisedCargo", "DryBulkAndUnitisedCargo"));
        typAuswahl.getSelectionModel().selectFirst();

        // Explizites data binding: das Label folgt der Status-Property
        statusLabel.textProperty().bind(statusText);

        setupDragAndDrop();
    }

    /**
     * Setzt die Geschaeftslogik (GL) und laedt die initialen Daten.
     */
    public void setWarehouseManager(WarehouseManager manager) {
        this.gl = manager;
        aktualisiereTabellen();
    }

    /**
     * Empfaengt Rueckmeldungen der GL (Feedback-Event) und zeigt sie im
     * Status-Label an. Kann auch aus Hintergrund-Threads aufgerufen werden.
     */
    @Override
    public void onFeedbackReceived(String feedback) {
        Platform.runLater(() -> statusText.set(feedback));
    }

    private void aktualisiereTabellen() {
        aktualisiereTabellen(null);
    }

    /**
     * Laedt die Tabellen neu; ein optionaler Typ-Filter (Klassenname)
     * schraenkt die Frachtliste ein (leer/null = alle).
     */
    private void aktualisiereTabellen(String typFilter) {
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
        java.util.Map<Integer, String> typenMap = gl.getCargoTypesMap();
        java.util.Map<Integer, Date> inspectionMap = gl.getInspectionDatesMap();
        java.util.Map<Integer, Date> insertionMap = gl.getInsertionDatesMap();

        for (java.util.Map.Entry<Integer, cargo.Cargo> entry : cargosMap.entrySet()) {
            int platz = entry.getKey();

            // Optionaler Typ-Filter wie im CLI (cargos [[Typ]])
            if (typFilter != null && !typFilter.isEmpty()
                    && !typFilter.equals(typenMap.get(platz))) {
                continue;
            }

            String kunde = ownersMap.get(platz).getName();
            Date inspektion = inspectionMap.get(platz);
            Date insertion = insertionMap.get(platz);

            // Einlagerungsdauer berechnen (Differenz aktuelles Datum - Einfuegedatum)
            long diffInMillies = insertion == null ? 0
                    : Math.abs(new Date().getTime() - insertion.getTime());
            Duration dauer = Duration.ofMillis(diffInMillies);

            frachtDaten.add(new CargoViewModel(platz, kunde, inspektion, dauer));
        }
    }

    // --- Aktionen (CRUD) ---

    @FXML
    private void handleKundeAnlegen() {
        String name = kundenNameInput.getText().trim();
        if (!name.isEmpty()) {
            gl.onInsertCustomer(name);
            aktualisiereTabellen();
            kundenNameInput.clear();
        }
    }

    @FXML
    private void handleKundeLoeschen() {
        String name = kundenNameInput.getText().trim();
        if (!name.isEmpty()) {
            gl.onDeleteCustomer(name);
            aktualisiereTabellen();
            kundenNameInput.clear();
        }
    }

    @FXML
    private void handleFrachtEinfuegen() {
        final String typ = typAuswahl.getValue();
        final String kunde = frachtKundeInput.getText().trim();
        if (typ == null || kunde.isEmpty()) {
            statusText.set("Fehler: Typ und Kunde angeben.");
            return;
        }

        final BigDecimal wert;
        final int grainSize;
        try {
            // Wert mit Dezimalkomma wie im CLI (z.B. 4004,50)
            wert = new BigDecimal(wertInput.getText().trim().replace(",", "."));
            grainSize = grainSizeInput.getText().trim().isEmpty()
                    ? 0 : Integer.parseInt(grainSizeInput.getText().trim());
        } catch (NumberFormatException e) {
            statusText.set("Fehler: Ungültige Zahleneingabe.");
            return;
        }

        // Gefahrenstoffe kommasepariert, leer = keine (wie im CLI)
        final List<String> hazards = new ArrayList<>();
        for (String h : hazardsInput.getText().split(",")) {
            if (!h.trim().isEmpty()) {
                hazards.add(h.trim());
            }
        }
        final boolean fragile = fragileInput.isSelected();

        // Nebenläufigkeit: das Einfügen sperrt die Oberfläche nicht, weitere
        // Aktionen (auch ein weiteres Einfügen) sind währenddessen möglich.
        Task<Void> insertTask = new Task<Void>() {
            @Override
            protected Void call() {
                gl.onInsertCargo(typ, kunde, wert, hazards, fragile, false, grainSize);
                return null;
            }
        };

        insertTask.setOnSucceeded(e -> {
            aktualisiereTabellen();
            frachtKundeInput.clear();
            wertInput.clear();
            hazardsInput.clear();
            grainSizeInput.clear();
            fragileInput.setSelected(false);
        });

        Thread thread = new Thread(insertTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleFrachtLoeschen() {
        try {
            int platz = Integer.parseInt(platzInput.getText().trim());
            gl.onDeleteCargo(platz);
            aktualisiereTabellen();
            platzInput.clear();
        } catch (NumberFormatException e) {
            statusText.set("Fehler: Bitte gültigen Lagerplatz eingeben.");
        }
    }

    @FXML
    private void handleInspektionSetzen() {
        try {
            int platz = Integer.parseInt(platzInput.getText().trim());
            gl.onUpdateInspectionDate(platz);
            aktualisiereTabellen();
        } catch (NumberFormatException e) {
            statusText.set("Fehler: Bitte gültigen Lagerplatz eingeben.");
        }
    }

    // --- Anzeigen (wie im CLI: cargos [[Typ]], hazards i/e) ---

    @FXML
    private void handleFrachtFiltern() {
        aktualisiereTabellen(typFilterInput.getText().trim());
    }

    @FXML
    private void handleHazardsVorhanden() {
        // Ausgabe erfolgt ueber das Feedback-Event im Status-Label
        gl.onReadHazards(true);
    }

    @FXML
    private void handleHazardsFehlend() {
        gl.onReadHazards(false);
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
