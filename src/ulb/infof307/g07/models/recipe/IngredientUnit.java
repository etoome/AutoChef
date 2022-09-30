package ulb.infof307.g07.models.recipe;

import java.util.Arrays;
import java.util.Optional;

/**
 * The unit for ingredients
 */
public enum IngredientUnit {
    UNIT("unité(s)"), ML("ml"), L("l"), G("g"), KG("kg");

    private final String name;

    IngredientUnit(String unitName) {
        this.name = unitName;
    }

    public String getUnitString() {
        return name;
    }

    public boolean isLowerThan(IngredientUnit otherUnit) {
        if (sameUnitRange(this, otherUnit)) {
            if (this == UNIT)
                return false;
            else
                return ((this == ML || this == G) && (otherUnit == L || otherUnit == KG));
        }
        return false;
    }

    public boolean isGreaterThan(IngredientUnit otherUnit) {
        return !isLowerThan(otherUnit);
    }

    public static boolean sameUnitRange(IngredientUnit unit1, IngredientUnit unit2) {
        return (unit1 == UNIT && unit2 == UNIT) || ((unit1 == ML || unit1 == L) && (unit2 == ML || unit2 == L))
                || ((unit1 == G || unit1 == KG) && (unit2 == G || unit2 == KG));
    }

    public static double convertValue(IngredientUnit from, IngredientUnit to) {
        if (sameUnitRange(from, to)) {
            if (from.isLowerThan(to))
                return 1000;
            else
                return 0.001;
        }
        return 1;
    }

    /** Get value from string */
    public static IngredientUnit getValue(String unitName) {
        Optional<IngredientUnit> oIng = Arrays.stream(IngredientUnit.values())
                .filter(unit -> unit.name.equals(unitName)).findFirst();
        return oIng.orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
