package io;

import domainLogic.WarehouseManager;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Implementierung der Persistenz mittels JavaBeans Persistence (JBP).
 * Verwendet XML zum Speichern des Zustands.
 */
public class JBPPersistence implements PersistenceStrategy {

    @Override
    public String getFilepath() {
        return "warehouse.xml";
    }

    @Override
    public void save(WarehouseManager manager, OutputStream out) throws Exception {
        try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out))) {

            // Persistence-Delegate für BigDecimal (Folie 58): BigDecimal besitzt
            // keinen parameterlosen Konstruktor, den der XMLEncoder voraussetzt.
            // Der Delegate weist ihn auf den String-Konstruktor new BigDecimal(String) hin.
            encoder.setPersistenceDelegate(BigDecimal.class, new DefaultPersistenceDelegate() {
                @Override
                protected Expression instantiate(Object oldInstance, Encoder out) {
                    return new Expression(oldInstance, BigDecimal.class, "new", new Object[]{oldInstance.toString()});
                }
            });

            // Es wird nicht die GL selbst serialisiert, sondern ein Snapshot
            // (Transferobjekt der Persistenzschicht). So braucht die GL keine
            // oeffentlichen Setter und ihre Kapselung bleibt erhalten.
            WarehouseSnapshot snapshot = new WarehouseSnapshot();
            snapshot.setCapacity(manager.getCapacity());
            snapshot.setNextLocation(manager.getNextLocation());
            snapshot.setCustomers(manager.getAllCustomers());
            snapshot.setCargos(manager.getCargosMap());
            snapshot.setCargoOwners(manager.getCargoOwnersMap());
            snapshot.setCargoTypes(manager.getCargoTypesMap());
            snapshot.setInsertionDates(manager.getInsertionDatesMap());
            snapshot.setInspectionDates(manager.getInspectionDatesMap());
            encoder.writeObject(snapshot);
        }
    }

    @Override
    public WarehouseManager load(InputStream in) throws Exception {
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in))) {
            // Der cast ist der technisch unvermeidbare beim Lesen aus einem stream.
            WarehouseSnapshot snapshot = (WarehouseSnapshot) decoder.readObject();
            return new WarehouseManager(snapshot.getCapacity(), snapshot.getNextLocation(),
                    snapshot.getCustomers(), snapshot.getCargos(), snapshot.getCargoOwners(),
                    snapshot.getCargoTypes(), snapshot.getInsertionDates(),
                    snapshot.getInspectionDates());
        }
    }
}
