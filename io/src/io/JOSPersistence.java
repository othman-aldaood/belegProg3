package io;

import domainLogic.WarehouseManager;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Implementierung der Persistenz mittels Java Object Serialization (JOS).
 */
public class JOSPersistence implements PersistenceStrategy {

    @Override
    public String getFilepath() {
        return "warehouse.ser";
    }

    @Override
    public void save(WarehouseManager manager, OutputStream out) throws Exception {
        // try-with-resources schließt die Streams automatisch (Folie 15)
        try (ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(manager);
        }
    }

    @Override
    public WarehouseManager load(InputStream in) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(in)) {
            // Cast ist beim Deserialisieren technisch notwendig
            return (WarehouseManager) ois.readObject();
        }
    }
}
