package domainLogic;

import administration.Customer;
import cargo.Cargo;
import cargo.Hazard;
import events.CapacityObserver;
import events.ChangeObserver;
import events.GLFeedbackListener;
import events.HazardObserver;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unittests fuer die Geschaeftslogik (WarehouseManager).
 * Rueckmeldungen und Beobachter-Signale werden ueber gemockte Listener
 * (Mockito) geprueft, Zustandsaenderungen ueber die Abfragemethoden.
 */
public class WarehouseManagerTest {

    // =========================================================================
    // --- Konstruktoren ---
    // =========================================================================

    @Test
    public void defaultKonstruktorSetztKapazitaetHundert() {
        WarehouseManager manager = new WarehouseManager();
        assertEquals(100, manager.getCapacity());
    }

    @Test
    public void konstruktorSetztUebergebeneKapazitaet() {
        WarehouseManager manager = new WarehouseManager(5);
        assertEquals(5, manager.getCapacity());
    }

    @Test
    public void getNextLocationStartetBeiEins() {
        WarehouseManager manager = new WarehouseManager(5);
        assertEquals(1, manager.getNextLocation());
    }

    @Test
    public void wiederherstellungsKonstruktorUebernimmtZustand() {
        Customer alice = new CustomerImpl("Alice");
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(alice, BigDecimal.ONE, null, 5));
        Map<Integer, Customer> owners = new HashMap<>();
        owners.put(1, alice);
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        Map<Integer, Date> insertions = new HashMap<>();
        insertions.put(1, new Date(1000L));
        Map<Integer, Date> inspections = new HashMap<>();
        inspections.put(1, new Date(2000L));
        WarehouseManager manager = new WarehouseManager(5, 2,
                Collections.singleton(alice), cargos, owners, types, insertions, inspections);
        assertEquals(1, manager.getCurrentSize());
    }

    @Test
    public void wiederherstellungsKonstruktorSetztNextLocation() {
        WarehouseManager manager = new WarehouseManager(5, 9, null, null, null, null, null, null);
        assertEquals(9, manager.getNextLocation());
    }

    @Test
    public void wiederherstellungsKonstruktorMitNullWertenErzeugtLeeresLager() {
        WarehouseManager manager = new WarehouseManager(5, 1, null, null, null, null, null, null);
        assertTrue(manager.isEmpty());
    }

    @Test
    public void wiederherstellungsKonstruktorMitNullKundinnenErzeugtLeereMenge() {
        WarehouseManager manager = new WarehouseManager(5, 1, null, null, null, null, null, null);
        assertTrue(manager.getAllCustomers().isEmpty());
    }

    // =========================================================================
    // --- Kundinnen anlegen ---
    // =========================================================================

    @Test
    public void onInsertCustomerMeldetErfolg() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: Kunde 'Alice' wurde angelegt.");
    }

    @Test
    public void onInsertCustomerMeldetFehlerBeiDoppeltemNamen() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCustomer("Alice");
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Kunde 'Alice' existiert bereits.");
    }

    @Test
    public void onInsertCustomerOhneListenerLegtKundinAn() {
        // deckt den Zweig ohne registrierten FeedbackListener ab (keine Ausgabe)
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        assertEquals(1, manager.getAllCustomers().size());
    }

    @Test
    public void onInsertCustomerMeldetFehlerBeiNullNamen() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer(null);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Kein Kundenname angegeben.");
    }

    @Test
    public void onInsertCustomerBenachrichtigtAenderungsBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.onInsertCustomer("Alice");
        Mockito.verify(observer).onChanged();
    }

    // =========================================================================
    // --- Frachtstuecke einfuegen ---
    // =========================================================================

    @Test
    public void onInsertCargoMeldetFehlerBeiVollemLager() {
        WarehouseManager manager = new WarehouseManager(0);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Das Lager ist voll!");
    }

    @Test
    public void onInsertCargoWarntObserverAbNeunzigProzent() {
        Cargo cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, cargo);
        cargos.put(2, cargo);
        cargos.put(3, cargo);
        cargos.put(4, cargo);
        cargos.put(5, cargo);
        cargos.put(6, cargo);
        cargos.put(7, cargo);
        cargos.put(8, cargo);
        cargos.put(9, cargo);
        WarehouseManager manager = new WarehouseManager(10, 10, null, cargos, null, null, null, null);
        CapacityObserver observer = Mockito.mock(CapacityObserver.class);
        manager.addCapacityObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Nobody", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(observer).onCapacityWarning();
    }

    @Test
    public void onInsertCargoWarntNichtUnterNeunzigProzent() {
        WarehouseManager manager = new WarehouseManager(10);
        CapacityObserver observer = Mockito.mock(CapacityObserver.class);
        manager.addCapacityObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Nobody", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(observer, Mockito.never()).onCapacityWarning();
    }

    @Test
    public void removeCapacityObserverEntferntBeobachter() {
        Cargo cargo = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, cargo);
        cargos.put(2, cargo);
        cargos.put(3, cargo);
        cargos.put(4, cargo);
        cargos.put(5, cargo);
        cargos.put(6, cargo);
        cargos.put(7, cargo);
        cargos.put(8, cargo);
        cargos.put(9, cargo);
        WarehouseManager manager = new WarehouseManager(10, 10, null, cargos, null, null, null, null);
        CapacityObserver observer = Mockito.mock(CapacityObserver.class);
        manager.addCapacityObserver(observer);
        manager.removeCapacityObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Nobody", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(observer, Mockito.never()).onCapacityWarning();
    }

    @Test
    public void onInsertCargoMeldetFehlerBeiUnbekannterKundin() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCargo("DryBulkCargo", "Bob", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Kunde 'Bob' nicht gefunden.");
    }

    @Test
    public void onInsertCargoLegtDryBulkCargoAn() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: DryBulkCargo auf Lagerplatz 1 eingefügt.");
    }

    @Test
    public void onInsertCargoLegtUnitisedCargoAn() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("UnitisedCargo", "Alice", BigDecimal.ONE, null, true, false, 0);
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: UnitisedCargo auf Lagerplatz 1 eingefügt.");
    }

    @Test
    public void onInsertCargoLegtDryBulkAndUnitisedCargoAn() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkAndUnitisedCargo", "Alice", BigDecimal.ONE, null, true, false, 5);
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: DryBulkAndUnitisedCargo auf Lagerplatz 1 eingefügt.");
    }

    @Test
    public void onInsertCargoMeldetFehlerBeiUnbekanntemTyp() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("LiquidBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Unbekannter Frachttyp.");
    }

    @Test
    public void onInsertCargoUebernimmtGefahrenstoffe() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE,
                Arrays.asList("flammable", " TOXIC "), false, false, 5);
        Cargo cargo = manager.getAllCargos().iterator().next();
        assertEquals(2, cargo.getHazards().size());
    }

    @Test
    public void onInsertCargoIgnoriertUngueltigeGefahrenstoffe() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE,
                Collections.singletonList("bogus"), false, false, 5);
        Cargo cargo = manager.getAllCargos().iterator().next();
        assertTrue(cargo.getHazards().isEmpty());
    }

    @Test
    public void onInsertCargoVergibtFortlaufendeLagerplaetze() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertEquals(3, manager.getNextLocation());
    }

    @Test
    public void onInsertCargoVergibtEinfuegedatum() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertNotNull(manager.getInsertionDatesMap().get(1));
    }

    @Test
    public void onInsertCargoBenachrichtigtAenderungsBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(observer).onChanged();
    }

    @Test
    public void onInsertCargoBenachrichtigtGefahrenstoffBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        HazardObserver observer = Mockito.mock(HazardObserver.class);
        manager.addHazardObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE,
                Collections.singletonList("flammable"), false, false, 5);
        Mockito.verify(observer).onHazardsChanged();
    }

    @Test
    public void onInsertCargoOhneGefahrenstoffeKeineGefahrenstoffBenachrichtigung() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        HazardObserver observer = Mockito.mock(HazardObserver.class);
        manager.addHazardObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        Mockito.verify(observer, Mockito.never()).onHazardsChanged();
    }

    @Test
    public void removeHazardObserverEntferntBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        HazardObserver observer = Mockito.mock(HazardObserver.class);
        manager.addHazardObserver(observer);
        manager.removeHazardObserver(observer);
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE,
                Collections.singletonList("flammable"), false, false, 5);
        Mockito.verify(observer, Mockito.never()).onHazardsChanged();
    }

    @Test
    public void removeChangeObserverEntferntBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.removeChangeObserver(observer);
        manager.onInsertCustomer("Alice");
        Mockito.verify(observer, Mockito.never()).onChanged();
    }

    // =========================================================================
    // --- Anzeigen (Kundinnen, Frachtstuecke, Gefahrenstoffe) ---
    // =========================================================================

    @Test
    public void onReadCustomersListetKundinMitAnzahl() {
        Customer alice = new CustomerImpl("Alice");
        Map<Integer, Customer> owners = new HashMap<>();
        owners.put(1, alice);
        WarehouseManager manager = new WarehouseManager(5, 2,
                Collections.singleton(alice), null, owners, null, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadCustomers();
        Mockito.verify(feedback).onFeedbackReceived("Kunden:\n- Alice (1)");
    }

    @Test
    public void onReadCargosOhneFilterListetAlle() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        cargos.put(2, new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true));
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        types.put(2, "UnitisedCargo");
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, null, types, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadCargos(null);
        Mockito.verify(feedback).onFeedbackReceived(Mockito.contains("Platz 2"));
    }

    @Test
    public void onReadCargosFiltertNachTyp() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        cargos.put(2, new UnitisedCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, true));
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        types.put(2, "UnitisedCargo");
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, null, types, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadCargos("DryBulkCargo");
        Mockito.verify(feedback).onFeedbackReceived(AdditionalMatchers.not(Mockito.contains("Platz 2")));
    }

    @Test
    public void onReadCargosMitLeeremFilterListetAlle() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, types, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadCargos("");
        Mockito.verify(feedback).onFeedbackReceived(Mockito.contains("Platz 1"));
    }

    @Test
    public void onReadCargosZeigtNullSekundenOhneEinfuegedatum() {
        // deckt den Zweig ab, in dem kein Einfuegedatum hinterlegt ist
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, types, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadCargos(null);
        Mockito.verify(feedback).onFeedbackReceived(Mockito.contains("Lagerdauer: 0s"));
    }

    @Test
    public void onReadHazardsZeigtVorhandeneGefahrenstoffe() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.FLAMMABLE), 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadHazards(true);
        Mockito.verify(feedback).onFeedbackReceived(Mockito.contains("FLAMMABLE"));
    }

    @Test
    public void onReadHazardsZeigtKeineBeiLeeremLager() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadHazards(true);
        Mockito.verify(feedback).onFeedbackReceived("Vorhandene Gefahrenstoffe: keine");
    }

    @Test
    public void onReadHazardsZeigtNichtVorhandeneOhneVorhandene() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE,
                Collections.singletonList(Hazard.FLAMMABLE), 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onReadHazards(false);
        Mockito.verify(feedback).onFeedbackReceived(AdditionalMatchers.not(Mockito.contains("FLAMMABLE")));
    }

    // =========================================================================
    // --- Inspektionsdatum setzen ---
    // =========================================================================

    @Test
    public void onUpdateInspectionDateMeldetErfolg() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onUpdateInspectionDate(1);
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: Inspektionsdatum aktualisiert.");
    }

    @Test
    public void onUpdateInspectionDateMeldetFehlerBeiUnbekanntemPlatz() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onUpdateInspectionDate(99);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Frachtstück nicht gefunden.");
    }

    @Test
    public void onUpdateInspectionDateBenachrichtigtAenderungsBeobachter() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.onUpdateInspectionDate(1);
        Mockito.verify(observer).onChanged();
    }

    // =========================================================================
    // --- Loeschen ---
    // =========================================================================

    @Test
    public void onDeleteCustomerMeldetFehlerBeiUnbekannterKundin() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onDeleteCustomer("Bob");
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Kunde 'Bob' nicht gefunden.");
    }

    @Test
    public void onDeleteCustomerEntferntKundin() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onDeleteCustomer("Alice");
        assertTrue(manager.getAllCustomers().isEmpty());
    }

    @Test
    public void onDeleteCustomerEntferntZugehoerigeFrachtstuecke() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.onDeleteCustomer("Alice");
        assertTrue(manager.isEmpty());
    }

    @Test
    public void onDeleteCustomerMeldetAnzahlGeloeschterFrachtstuecke() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.onDeleteCustomer("Alice");
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: Kunde 'Alice' und 1 Frachtstueck(e) geloescht.");
    }

    @Test
    public void onDeleteCustomerBenachrichtigtGefahrenstoffBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE,
                Collections.singletonList("flammable"), false, false, 5);
        HazardObserver observer = Mockito.mock(HazardObserver.class);
        manager.addHazardObserver(observer);
        manager.onDeleteCustomer("Alice");
        Mockito.verify(observer).onHazardsChanged();
    }

    @Test
    public void onDeleteCargoEntferntFrachtstueck() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.onDeleteCargo(1);
        assertEquals(0, manager.getCurrentSize());
    }

    @Test
    public void onDeleteCargoMeldetErfolg() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.onDeleteCargo(1);
        Mockito.verify(feedback).onFeedbackReceived("Erfolg: Frachtstück auf Platz 1 gelöscht.");
    }

    @Test
    public void onDeleteCargoMeldetFehlerBeiUnbekanntemPlatz() {
        WarehouseManager manager = new WarehouseManager(5);
        GLFeedbackListener feedback = Mockito.mock(GLFeedbackListener.class);
        manager.setFeedbackListener(feedback);
        manager.onDeleteCargo(99);
        Mockito.verify(feedback).onFeedbackReceived("Fehler: Frachtstück nicht gefunden.");
    }

    @Test
    public void onDeleteCargoBenachrichtigtAenderungsBeobachter() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.onDeleteCargo(1);
        Mockito.verify(observer).onChanged();
    }

    // =========================================================================
    // --- Abfragemethoden inkl. Kapselung ---
    // =========================================================================

    @Test
    public void getAllCustomersLiefertKopie() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.getAllCustomers().clear();
        assertEquals(1, manager.getAllCustomers().size());
    }

    @Test
    public void getAllCargosLiefertKopie() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.getAllCargos().clear();
        assertEquals(1, manager.getAllCargos().size());
    }

    @Test
    public void getAllCustomersKopienSchuetzenVorNamensAenderung() {
        // Kapselungstest nach Belegvorgabe: sichtbare setter an den
        // zurueckgegebenen Objekten duerfen die GL nicht veraendern.
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.getAllCustomers().iterator().next().setName("Hacked");
        assertEquals("Alice", manager.getAllCustomers().iterator().next().getName());
    }

    @Test
    public void getAllCargosKopienSchuetzenVorInspektionsAenderung() {
        // Kapselungstest nach Belegvorgabe: sichtbare setter an den
        // zurueckgegebenen Objekten duerfen die GL nicht veraendern.
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        manager.getAllCargos().iterator().next().setLastInspectionDate(new Date(999L));
        assertNull(manager.getAllCargos().iterator().next().getLastInspectionDate());
    }

    @Test
    public void getAllCargosSetztLagerplatzAufDenKopien() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertEquals(1, manager.getAllCargos().iterator().next().getStorageLocation());
    }

    @Test
    public void getInspectionDatesMapKopienSchuetzenVorAenderung() {
        Map<Integer, Date> dates = new HashMap<>();
        dates.put(1, new Date(2000L));
        WarehouseManager manager = new WarehouseManager(5, 2, null, null, null, null, null, dates);
        manager.getInspectionDatesMap().get(1).setTime(5L);
        assertEquals(new Date(2000L), manager.getInspectionDatesMap().get(1));
    }

    @Test
    public void getStorageLocationsLiefertVergebenePlaetze() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertEquals(Collections.singletonList(1), manager.getStorageLocations());
    }

    @Test
    public void isEmptyLiefertTrueBeiLeeremLager() {
        WarehouseManager manager = new WarehouseManager(5);
        assertTrue(manager.isEmpty());
    }

    @Test
    public void isEmptyLiefertFalseNachEinfuegen() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertFalse(manager.isEmpty());
    }

    @Test
    public void getCurrentSizeZaehltFrachtstuecke() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        assertEquals(1, manager.getCurrentSize());
    }

    @Test
    public void getCargoCountForCustomerZaehltFrachtstuecke() {
        Customer alice = new CustomerImpl("Alice");
        Map<Integer, Customer> owners = new HashMap<>();
        owners.put(1, alice);
        owners.put(2, alice);
        WarehouseManager manager = new WarehouseManager(5, 3,
                Collections.singleton(alice), null, owners, null, null, null);
        assertEquals(2, manager.getCargoCountForCustomer(alice));
    }

    @Test
    public void getCargoTypesMapLiefertTypen() {
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        WarehouseManager manager = new WarehouseManager(5, 2, null, null, null, types, null, null);
        assertEquals("DryBulkCargo", manager.getCargoTypesMap().get(1));
    }

    @Test
    public void getCargoTypesMapLiefertKopie() {
        Map<Integer, String> types = new HashMap<>();
        types.put(1, "DryBulkCargo");
        WarehouseManager manager = new WarehouseManager(5, 2, null, null, null, types, null, null);
        manager.getCargoTypesMap().clear();
        assertEquals(1, manager.getCargoTypesMap().size());
    }

    // =========================================================================
    // --- Lagerplatztausch (drag&drop der GUI) ---
    // =========================================================================

    @Test
    public void swapLagerplatzLiefertFalseBeiUnbekanntemErstenPlatz() {
        WarehouseManager manager = new WarehouseManager(5);
        assertFalse(manager.swapLagerplatz(1, 2));
    }

    @Test
    public void swapLagerplatzLiefertFalseBeiUnbekanntemZweitenPlatz() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        WarehouseManager manager = new WarehouseManager(5, 2, null, cargos, null, null, null, null);
        assertFalse(manager.swapLagerplatz(1, 2));
    }

    @Test
    public void swapLagerplatzLiefertTrueBeiErfolg() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        cargos.put(2, new UnitisedCargoImpl(new CustomerImpl("Bob"), BigDecimal.ONE, null, true));
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, null, null, null, null);
        assertTrue(manager.swapLagerplatz(1, 2));
    }

    @Test
    public void swapLagerplatzTauschtFrachtstuecke() {
        Cargo first = new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5);
        Cargo second = new UnitisedCargoImpl(new CustomerImpl("Bob"), BigDecimal.TEN, null, true);
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, first);
        cargos.put(2, second);
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, null, null, null, null);
        manager.swapLagerplatz(1, 2);
        assertEquals(BigDecimal.ONE, manager.getCargosMap().get(2).getValue());
    }

    @Test
    public void swapLagerplatzTauschtEigentuemerinnen() {
        Customer alice = new CustomerImpl("Alice");
        Customer bob = new CustomerImpl("Bob");
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(alice, BigDecimal.ONE, null, 5));
        cargos.put(2, new UnitisedCargoImpl(bob, BigDecimal.ONE, null, true));
        Map<Integer, Customer> owners = new HashMap<>();
        owners.put(1, alice);
        owners.put(2, bob);
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, owners, null, null, null);
        manager.swapLagerplatz(1, 2);
        assertEquals(bob, manager.getCargoOwnersMap().get(1));
    }

    @Test
    public void swapLagerplatzBenachrichtigtAenderungsBeobachter() {
        Map<Integer, Cargo> cargos = new HashMap<>();
        cargos.put(1, new DryBulkCargoImpl(new CustomerImpl("Alice"), BigDecimal.ONE, null, 5));
        cargos.put(2, new UnitisedCargoImpl(new CustomerImpl("Bob"), BigDecimal.ONE, null, true));
        WarehouseManager manager = new WarehouseManager(5, 3, null, cargos, null, null, null, null);
        ChangeObserver observer = Mockito.mock(ChangeObserver.class);
        manager.addChangeObserver(observer);
        manager.swapLagerplatz(1, 2);
        Mockito.verify(observer).onChanged();
    }

    // =========================================================================
    // --- Map-Abfragen fuer GUI, Simulationen und Persistenz ---
    // =========================================================================

    @Test
    public void getCargosMapLiefertKopie() {
        WarehouseManager manager = new WarehouseManager(5);
        manager.onInsertCustomer("Alice");
        manager.onInsertCargo("DryBulkCargo", "Alice", BigDecimal.ONE, null, false, false, 5);
        manager.getCargosMap().clear();
        assertEquals(1, manager.getCargosMap().size());
    }

    @Test
    public void getCargoOwnersMapLiefertEigentuemerin() {
        Customer alice = new CustomerImpl("Alice");
        Map<Integer, Customer> owners = new HashMap<>();
        owners.put(1, alice);
        WarehouseManager manager = new WarehouseManager(5, 2,
                Collections.singleton(alice), null, owners, null, null, null);
        assertEquals(alice, manager.getCargoOwnersMap().get(1));
    }

    @Test
    public void getInspectionDatesMapLiefertDaten() {
        Map<Integer, Date> dates = new HashMap<>();
        dates.put(1, new Date(2000L));
        WarehouseManager manager = new WarehouseManager(5, 2, null, null, null, null, null, dates);
        assertEquals(new Date(2000L), manager.getInspectionDatesMap().get(1));
    }

    @Test
    public void getInsertionDatesMapLiefertDaten() {
        Map<Integer, Date> dates = new HashMap<>();
        dates.put(1, new Date(1000L));
        WarehouseManager manager = new WarehouseManager(5, 2, null, null, null, null, dates, null);
        assertEquals(new Date(1000L), manager.getInsertionDatesMap().get(1));
    }
}
