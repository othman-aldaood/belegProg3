package log;

import events.CargoCommandListener;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Protokolliert jede Benutzerinteraktion, die die Geschaeftslogik erreicht,
 * und leitet den Befehl anschliessend unveraendert weiter.
 * Die Klasse wird im setup zwischen Oberflaeche und Geschaeftslogik gehaengt
 * (Decorator). Weder die Oberflaeche noch die Geschaeftslogik kennen das Log,
 * die bestehende Implementierung bleibt davon unabhaengig.
 */
public class LoggingCommandListener implements CargoCommandListener {

    private final CargoCommandListener delegate;
    private final LogWriter writer;
    private final LogTexts texte;

    /**
     * Erzeugt den protokollierenden Stellvertreter.
     *
     * @param delegate die eigentliche Geschaeftslogik
     * @param writer   die Ausgabe der Logeintraege
     * @param texte    die Textquelle der gewaehlten Sprache
     */
    public LoggingCommandListener(CargoCommandListener delegate, LogWriter writer, LogTexts texte) {
        this.delegate = delegate;
        this.writer = writer;
        this.texte = texte;
    }

    @Override
    public void onInsertCustomer(String customerName) {
        this.writer.write(this.texte.get(LogTexts.INSERT_CUSTOMER));
        this.delegate.onInsertCustomer(customerName);
    }

    @Override
    public void onInsertCargo(String type, String customerName, BigDecimal value,
                              Collection<String> hazards, boolean isFragile,
                              boolean isPressurized, int grainSize) {
        this.writer.write(this.texte.get(LogTexts.INSERT_CARGO));
        this.delegate.onInsertCargo(type, customerName, value, hazards, isFragile, isPressurized, grainSize);
    }

    @Override
    public void onReadCustomers() {
        this.writer.write(this.texte.get(LogTexts.READ_CUSTOMERS));
        this.delegate.onReadCustomers();
    }

    @Override
    public void onReadCargos(String cargoType) {
        this.writer.write(this.texte.get(LogTexts.READ_CARGOS));
        this.delegate.onReadCargos(cargoType);
    }

    @Override
    public void onReadHazards(boolean existing) {
        this.writer.write(this.texte.get(LogTexts.READ_HAZARDS));
        this.delegate.onReadHazards(existing);
    }

    @Override
    public void onUpdateInspectionDate(int storageLocation) {
        this.writer.write(this.texte.get(LogTexts.UPDATE_INSPECTION));
        this.delegate.onUpdateInspectionDate(storageLocation);
    }

    @Override
    public void onDeleteCustomer(String customerName) {
        this.writer.write(this.texte.get(LogTexts.DELETE_CUSTOMER));
        this.delegate.onDeleteCustomer(customerName);
    }

    @Override
    public void onDeleteCargo(int storageLocation) {
        this.writer.write(this.texte.get(LogTexts.DELETE_CARGO));
        this.delegate.onDeleteCargo(storageLocation);
    }
}
