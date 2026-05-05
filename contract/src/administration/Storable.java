package administration;

import java.time.Duration;
import java.util.Date;

public interface Storable {
    Customer getOwner();
    /**
     * liefert die vergangene Zeit seit dem Einfügen
     * @return vergangene Zeit oder null wenn kein Einfügedatum gesetzt
     */
    Duration getDurationOfStorage();
    Date getLastInspectionDate();
    int getStorageLocation();
}
