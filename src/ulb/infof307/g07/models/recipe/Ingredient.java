package ulb.infof307.g07.models.recipe;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.ingredient.IngredientDAO;

/**
 * Ingredient model, used in a recipe to represent ingredients... & link implicitly link to products. Has a name, & a
 * quantity, who uses a Quantity object
 */
public class Ingredient {
    private String name;
    private final Quantity quantity;

    /**
     * Main constructor of an ingredient, who takes a name & a quantity object, who can't be null
     *
     * @param name:
     *            name of the ingredient
     * @param quantity:
     *            quantity of the ingredient
     */
    public Ingredient(String name, Quantity quantity) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        if (quantity.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity cannot be negative!");
        }
        if (quantity.getUnit() == null) {
            throw new IllegalArgumentException("Units cannot be null!");
        }
        this.name = name;
        this.quantity = quantity;
    }

    public Ingredient(String name) throws IllegalArgumentException {
        this(name, new Quantity(1F, IngredientUnit.UNIT));
    }

    public Ingredient(String name, Float quantity, IngredientUnit unit) throws IllegalArgumentException {
        this(name, new Quantity(quantity, unit));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Can't set a null name to an Ingredient");
        }
        this.name = name;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Float getQuantityValue() {
        return quantity.getQuantity();
    }

    public void setQuantity(float quantity) {
        this.quantity.setQuantity(quantity);
    }

    public IngredientUnit getUnit() {
        return quantity.getUnit();
    }

    public void setUnit(IngredientUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Can't est a null unit to an Ingredient");
        }
        this.quantity.setUnit(unit);
    }

    public static IngredientDAO getDAO() {
        return DAO.getInstance().getIngredientDAO();
    }
}