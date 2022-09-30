package ulb.infof307.g07.database.dao.store;

import org.apache.commons.lang.NotImplementedException;
import ulb.infof307.g07.models.recipe.Quantity;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StoreMockDAO implements StoreDAO {
    HashMap<Integer, Store> idToStore;
    HashMap<Store, HashMap<Product, Float>> productPriceByStore;

    public StoreMockDAO() {
        idToStore = new HashMap<>();
        productPriceByStore = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            idToStore.put(i + 1, new Store(String.format("Store %o", i + 1), i, i));
            productPriceByStore.put(idToStore.get(i + 1), new HashMap<>());
        }

        for (int i = 0; i < 3; i++) {
            productPriceByStore.get(idToStore.get(i + 1))
                    .put(new Product("creme", Product.Category.BAKERY, (float) i + 1), (float) i + 1);
            productPriceByStore.get(idToStore.get(i + 1))
                    .put(new Product("oeuf", Product.Category.BAKERY, (float) i + 1), (float) i + 1);
            productPriceByStore.get(idToStore.get(i + 1))
                    .put(new Product("eau", Product.Category.BAKERY, (float) i + 1), (float) i + 1);
            try {
                idToStore.get(i + 1).addProduct(new Product("creme", Product.Category.BAKERY));
                idToStore.get(i + 1).addProduct(new Product("oeuf", Product.Category.BAKERY));
                idToStore.get(i + 1).addProduct(new Product("eau", Product.Category.BAKERY));
            } catch (StoreException ignored) {
            }
        }

    }

    @Override
    public boolean isStoreAlreadyInDb(Store store) {
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            if (entry.getValue().equals(store)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Integer getIdFromStore(Store store) {
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            if (entry.getValue().equals(store)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean createStore(Store store) {
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            if (entry.getValue().equals(store)) {
                return false;
            }
        }
        idToStore.put(idToStore.size() + 1, store);
        return true;
    }

    @Override
    public void deleteStore(Store store) {
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            if (entry.getValue().equals(store)) {
                idToStore.remove(entry.getKey());
            }
        }
    }

    @Override
    public ArrayList<Store> getAllStores() {
        ArrayList<Store> res = new ArrayList<>();
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            res.add(entry.getValue());
        }
        return res;
    }

    @Override
    public Store getStore(String name) {
        for (Map.Entry<Integer, Store> entry : idToStore.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public ArrayList<Product> getProductsInStore(String name) {
        if (name == null || getStore(name) == null) {
            return null;
        }
        return getStore(name).getProducts();
    }

    @Override
    public void addProductInStore(Product product, Store store) {
        try {
            store.addProduct(product);
        } catch (StoreException ignored) {
        }
    }

    @Override
    public ArrayList<Store> getStoresWithAllProductsFrom(ShoppingList shopping_list) {
        if (shopping_list == null) {
            return null;
        }
        ArrayList<Store> res = new ArrayList<>();
        for (Map.Entry<Integer, Store> store : idToStore.entrySet()) {
            for (Map.Entry<Product, Quantity> entry : shopping_list.getProducts().entrySet()) {
                if (!store.getValue().hasProduct(entry.getKey())) {
                    break;
                }
            }
            res.add(store.getValue());

        }
        return res;
    }

    @Override
    public ArrayList<Store> getStoresWithoutAllProductsFrom(ShoppingList shoppingList) throws StoreException {
        throw new NotImplementedException("no time to implement");
    }

    @Override
    public HashMap<Store, Float> getListOfPriceByStore(ShoppingList shopping_list) {
        throw new NotImplementedException("no time to implement");
    }

    @Override
    public boolean isProductInStore(Product product, Store store) {
        return store.hasProduct(product);
    }

    @Override
    public boolean isProductInStore(Product product, Store store, Float price) {
        if (!store.hasProduct(product)) {
            return false;
        }
        for (Map.Entry<Product, Float> entry : productPriceByStore.get(store).entrySet()) {
            if (product.equals(entry.getKey())) {
                return entry.getValue().equals(price);
            }
        }
        return false;
    }

}
