package ulb.infof307.g07.database.dao.product;

import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductMockDAO implements ProductDAO {
    HashMap<Integer, Product> idToProduct;
    HashMap<String, Product> nameToProduct;

    public ProductMockDAO() {
        idToProduct = new HashMap<>();
        nameToProduct = new HashMap<>();
        ArrayList<String> names = new ArrayList<>();
        names.add("mayonnaise");
        names.add("sauce");
        names.add("olive");
        names.add("soupe");
        names.add("crouton");
        names.add("mouton");
        names.add("boeuf");
        names.add("Victor");
        names.add("creme brulee");
        names.add("mousse");

        for (int i = 0; i < 10; i++) {
            idToProduct.put(i + 1, new Product(names.get(i), Product.Category.BAKERY));
            nameToProduct.put(names.get(i), new Product(names.get(i), Product.Category.BAKERY));
        }

    }

    @Override
    public Integer getIdFromProduct(Product product) {
        for (Map.Entry<Integer, Product> entry : idToProduct.entrySet()) {
            if (entry.getValue().equals(product)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Integer getIdFromProduct(String name) {
        return getIdFromProduct(nameToProduct.get(name));
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(idToProduct.values());

    }

    @Override
    public boolean isProductAlreadyInDb(Product product) {
        for (Map.Entry<Integer, Product> entry : idToProduct.entrySet()) {
            if (entry.getValue().equals(product)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean createProduct(Product product) {
        if (isProductAlreadyInDb(product)) {
            return false;
        }
        idToProduct.put(idToProduct.size() + 1, product);
        nameToProduct.put(product.getName(), product);
        return true;
    }

    @Override
    public Product getProduct(String name) throws ProductException {
        return nameToProduct.get(name);
    }
}
