package ulb.infof307.g07.database.dao.ingredient;

import ulb.infof307.g07.models.recipe.exceptions.IngredientException;
import ulb.infof307.g07.models.shoppinglist.Product;

public interface IngredientDAO {
    Product getProductFromIngredient(int ingredientId) throws IngredientException;

    Product getProductFromIngredient(String ingredientName) throws IngredientException;

    int getIngredientIdFromName(String ingredientName) throws IngredientException;
}
