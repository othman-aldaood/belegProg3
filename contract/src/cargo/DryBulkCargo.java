package cargo;

import administration.Storable;

public interface DryBulkCargo extends Cargo, Storable {
    int getGrainSize();
}
