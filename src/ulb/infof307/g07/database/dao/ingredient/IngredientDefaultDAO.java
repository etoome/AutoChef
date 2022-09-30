package ulb.infof307.g07.database.dao.ingredient;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.recipe.exceptions.IngredientException;
import ulb.infof307.g07.models.shoppinglist.Product;

import java.util.List;

public class IngredientDefaultDAO implements IngredientDAO {

    @Override
    public Product getProductFromIngredient(int ingredientId) throws IngredientException {
        var db = Database.getInstance();

        try {
            return db.query(
                    "SELECT P.name, P.category FROM Ingredient as I JOIN ProductIngredient as PI ON I.id = PI.ingredient_id JOIN Product as P ON P.id = PI.product_id WHERE I.id = ?",
                    List.of(ingredientId), result -> result.next()
                            ? new Product(result.getString(1), Product.Category.valueOf(result.getString(2))) : null);
        } catch (DatabaseException e) {
            throw new IngredientException("Error when getting a product from an ingredient ", e);
        }
    }

    @Override
    public Product getProductFromIngredient(String ingredientName) throws IngredientException {
        return getProductFromIngredient(getIngredientIdFromName(ingredientName));
    }

    @Override
    public int getIngredientIdFromName(String ingredientName) throws IngredientException {
        var db = Database.getInstance();
        try {
            return db.query("SELECT id FROM Ingredient WHERE name=?;", List.of(ingredientName),
                    result -> result.next() ? result.getInt("id") : null);
        } catch (DatabaseException e) {
            throw new IngredientException("Error when getting ingredient's id from his name ", e);
        }
    }
}
