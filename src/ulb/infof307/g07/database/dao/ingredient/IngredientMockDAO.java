package ulb.infof307.g07.database.dao.ingredient;

import ulb.infof307.g07.models.shoppinglist.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientMockDAO implements IngredientDAO {
    HashMap<Integer, Product> ingredientIdToProduct;
    HashMap<String, Integer> ingredientNameToIngredientId;

    public IngredientMockDAO() {
        ingredientIdToProduct = new HashMap<>();
        ingredientNameToIngredientId = new HashMap<>();
        ArrayList<String> names = new ArrayList<>();
        names.add("mayonnaise");
        names.add("sauce");
        names.add("olive");
        names.add("soupe");
        names.add("crouton");
        names.add("mouton");
        names.add("boeuf");
        names.add("mousse");
        names.add("creme brulee");
        names.add("mousse");
        for (int i = 0; i < 10; i++) {
            ingredientIdToProduct.put(i + 1, new Product(names.get(i), Product.Category.BAKERY));
            ingredientNameToIngredientId.put(names.get(i), i + 1);
        }

    }

    @Override
    public Product getProductFromIngredient(int ingredientId) {
        return ingredientIdToProduct.get(ingredientId);
    }

    @Override
    public Product getProductFromIngredient(String ingredientName) {
        var id = ingredientNameToIngredientId.get(ingredientName);
        if (id == null) {
            return null;
        }
        return ingredientIdToProduct.get(id);
    }

    @Override
    public int getIngredientIdFromName(String ingredientName) {
        return ingredientNameToIngredientId.get(ingredientName);
    }
}
