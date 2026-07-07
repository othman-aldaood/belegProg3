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

            // WICHTIG (Folie 58): Fix für BigDecimal!
            // Da BigDecimal keinen Standardkonstruktor () hat, stürzt der XMLEncoder ab.
            // Wir sagen ihm hier, dass er den String-Konstruktor: new BigDecimal(String) nutzen soll.
            encoder.setPersistenceDelegate(BigDecimal.class, new DefaultPersistenceDelegate() {
                @Override
                protected Expression instantiate(Object oldInstance, Encoder out) {
                    return new Expression(oldInstance, BigDecimal.class, "new", new Object[]{oldInstance.toString()});
                }
            });

            encoder.writeObject(manager);
        }
    }

    @Override
    public WarehouseManager load(InputStream in) throws Exception {
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in))) {
            return (WarehouseManager) decoder.readObject();
        }
    }
}
