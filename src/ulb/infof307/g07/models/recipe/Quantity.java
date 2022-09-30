package ulb.infof307.g07.models.recipe;

import ulb.infof307.g07.models.recipe.exceptions.QuantityException;

/**
 * Used to simplify the quantity of an ingredient, used a float quantity and an IngredientUnit as the unit
 */
public class Quantity {
    private float value;
    private IngredientUnit unit;

    public Quantity(float quantity, IngredientUnit unit) {
        this.value = quantity;
        this.unit = unit;
    }

    public float getQuantity() {
        return value;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setQuantity(float value) {
        if (value <= 0) {
            throw new QuantityException("Can't set product quantity to negative or zero quantity");
        }
        this.value = value;
    }

    public void setUnit(IngredientUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Can't set a null unit to a quantity");
        }
        this.unit = unit;
    }

    public void add(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Can't add a null quantity to another quantity");
        }
        this.value += other.getQuantity();
        if (this.unit != other.unit)
            other.convertTo(unit);
    }

    public void sub(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Can't subtract a null quantity to another quantity");
        }
        this.value -= other.getQuantity();
        if (this.unit != other.unit)
            other.convertTo(unit);
    }

    public void convertTo(IngredientUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("can't set a null IngredientUnit to a quantity");
        }
        if (IngredientUnit.sameUnitRange(this.unit, unit)) {
            this.value *= IngredientUnit.convertValue(this.unit, unit);
        } else {
            throw new QuantityException(String.format("can't convert %s to %s", this.unit.name(), unit.name()));
        }
    }

}