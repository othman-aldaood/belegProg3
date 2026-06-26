package gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View-Model fuer Kunden (Customer).
 * Kapselt die Kundendaten in JavaFX-Properties fuer die GUI.
 */
public class CustomerViewModel {

    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty anzahlFrachtstuecke = new SimpleIntegerProperty();

    /**
     * Konstruktor zur Initialisierung der Kunden-Properties.
     */
    public CustomerViewModel(String kundenName, int anzahl) {
        this.name.set(kundenName);
        this.anzahlFrachtstuecke.set(anzahl);
    }

    // --- Getter fuer die JavaFX-Properties ---

    public StringProperty nameProperty() {
        return this.name;
    }

    public IntegerProperty anzahlFrachtstueckeProperty() {
        return this.anzahlFrachtstuecke;
    }
}