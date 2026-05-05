package cargo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

public interface Cargo extends Serializable {
    BigDecimal getValue();
    Collection<Hazard> getHazards();
}
