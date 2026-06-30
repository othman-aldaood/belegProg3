package gui;

import javafx.beans.property.*;
import java.time.Duration;
import java.util.Date;

/**
 * View-Model fuer Frachtstuecke (Cargo).
 * Kapselt die Daten in JavaFX-Properties fuer das Data-Binding in der GUI.
 * Dies garantiert eine strikte Trennung (Separation of Concerns).
 */
public class CargoViewModel {

    private final IntegerProperty lagerplatz = new SimpleIntegerProperty();
    private final StringProperty kundenName = new SimpleStringProperty();
    private final ObjectProperty<Date> inspektionsDatum = new SimpleObjectProperty<>();
    private final ObjectProperty<Duration> einlagerungsDauer = new SimpleObjectProperty<>();

    /**
     * Konstruktor zur Initialisierung der Properties mit primitiven Daten oder Standard-Objekten.
     */
    public CargoViewModel(int platz, String kunde, Date inspektion, Duration dauer) {
        this.lagerplatz.set(platz);
        this.kundenName.set(kunde);
        this.inspektionsDatum.set(inspektion);
        this.einlagerungsDauer.set(dauer);
    }

    // --- Getter fuer die JavaFX-Properties (Notwendig fuer PropertyValueFactory) ---

    public IntegerProperty lagerplatzProperty() {
        return this.lagerplatz;
    }

    public StringProperty kundenNameProperty() {
        return this.kundenName;
    }

    public ObjectProperty<Date> inspektionsDatumProperty() {
        return this.inspektionsDatum;
    }

    public ObjectProperty<Duration> einlagerungsDauerProperty() {
        return this.einlagerungsDauer;
    }
}