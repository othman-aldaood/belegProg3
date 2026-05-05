package cargo;

import administration.Storable;

public interface UnitisedCargo extends Cargo, Storable {
    boolean isFragile();
}
