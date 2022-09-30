package ulb.infof307.g07.database.dao.store;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoreDefaultDAO implements StoreDAO {

    @Override
    public boolean isStoreAlreadyInDb(Store store) throws StoreException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT * FROM Store WHERE name=? AND lat=? AND lng=?",
                    List.of(store.getName(), store.getLat(), store.getLng()), ResultSet::next);
        } catch (DatabaseException ignored) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }

    @Override
    public Integer getIdFromStore(Store store) throws StoreException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Store WHERE name=? AND lat=? AND lng=?",
                    List.of(store.getName(), store.getLat(), store.getLng()),
                    result -> result.next() ? result.getInt("id") : null);
        } catch (DatabaseException e) {
            throw new StoreException("Error when getting id from a store ", e);
        }
    }

    @Override
    public boolean createStore(Store store) throws StoreException {
        var db = Database.getInstance();

        try {
            if (isStoreAlreadyInDb(store))
                return false;
            var idInsertedStore = db.query("INSERT INTO Store(name, lat, lng) VALUES(?, ?, ?);",
                    List.of(store.getName(), store.getLat(), store.getLng()), Statement.RETURN_GENERATED_KEYS,
                    result -> result.next() ? result.getInt(1) : null);
            int currentProductId;
            try {
                for (Product product : store.getProducts()) {
                    currentProductId = Product.getDAO().getIdFromProduct(product);
                    if (currentProductId == -1 || currentProductId == 0) {
                        // Product is not in DB
                        Product.getDAO().createProduct(product);
                    }
                    db.query("INSERT INTO StoreProduct(store_id, product_id) VALUES(?,?);",
                            List.of(idInsertedStore, currentProductId));
                }
            } catch (ProductException e) {
                throw new StoreException("Failed to create the store", e);
            }
            return true;
        } catch (DatabaseException e) {
            throw new StoreException("Error when creating a store ", e);
        }
    }

    @Override
    public void deleteStore(Store store) throws StoreException {
        var db = Database.getInstance();
        int id = getIdFromStore(store);

        try {
            db.begin();
            db.query("DELETE FROM Store WHERE id=?", List.of(id));
            db.query("DELETE FROM StoreProduct WHERE store_id=?", List.of(id));
            db.commit();
        } catch (DatabaseException e) {
            throw new StoreException("Error when deleting a store ", e);
        }
    }

    @Override
    public ArrayList<Store> getAllStores() throws StoreException {
        var db = Database.getInstance();
        var res = new ArrayList<Store>();

        try {
            db.query("SELECT name, lat, lng FROM Store", result -> {
                while (result.next()) {
                    res.add(new Store(result.getString("name"), result.getDouble("lat"), result.getDouble("lng")));
                }
                return null;
            });
            return res;
        } catch (DatabaseException e) {
            throw new StoreException("Error when getting all stores ", e);
        }
    }

    @Override
    public Store getStore(String name) throws StoreException {
        var db = Database.getInstance();

        try {
            var store = db.query("SELECT name, lat, lng FROM Store WHERE name=?", List.of(name), result -> {
                if (result.next()) {
                    return new Store(result.getString("name"), result.getDouble("lat"), result.getDouble("lng"));
                } else {
                    return null;
                }
            });
            if (store == null) {
                throw new StoreException("Store with name: %s was not found.".formatted(name));
            }
            return store;
        } catch (DatabaseException e) {
            throw new StoreException("Error when getting a store ", e);
        }
    }

    @Override
    public ArrayList<Product> getProductsInStore(String name) throws StoreException {
        var db = Database.getInstance();
        var res = new ArrayList<Product>();

        try {
            db.query(
                    "SELECT name, category, price FROM Product P JOIN StoreProduct SP ON P.id = SP.product_id WHERE SP.store_id=? ORDER BY category ASC",
                    List.of(getIdFromStore(getStore(name))), result -> {
                        while (result.next()) {
                            res.add(new Product(result.getString("name"),
                                    Product.Category.valueOf(result.getString("category")), result.getFloat("price")));
                        }
                        return null;
                    });
            return res;
        } catch (DatabaseException e) {
            throw new StoreException("Error when getting products in store ", e);
        }
    }

    @Override
    public void addProductInStore(Product product, Store store) throws StoreException {
        var db = Database.getInstance();

        try {
            if (Product.getDAO().getIdFromProduct(product) == null)
                Product.getDAO().createProduct(product);
            float productPrice = product.getPrice();
            int storeId = Store.getDAO().getIdFromStore(store);
            int productId = Product.getDAO().getIdFromProduct(product);
            db.query("INSERT INTO StoreProduct(store_id, product_id, price) VALUES(?, ?, ?)",
                    List.of(storeId, productId, productPrice));
        } catch (DatabaseException | ProductException e) {
            throw new StoreException("Error when adding product in store ", e);
        }
    }

    @Override
    public ArrayList<Store> getStoresWithAllProductsFrom(ShoppingList shoppingList) throws StoreException {
        var db = Database.getInstance();
        ArrayList<Store> res = new ArrayList<>();

        try {
            int shoppingListId = ShoppingList.getDAO().getShoppingListIdFromName(shoppingList.getName());
            db.query(
                    "SELECT S.id, S.name FROM Store S WHERE NOT EXISTS (SELECT * FROM ProductShoppingList PSL WHERE PSL.shopping_list_id=? AND NOT EXISTS (SELECT * FROM StoreProduct SP WHERE SP.product_id = PSL.product_id AND SP.store_id = S.id ))",
                    List.of(shoppingListId), result -> {
                        while (result.next()) {
                            try {
                                res.add(getStore(result.getString(2)));
                            } catch (StoreException ignored) {
                                // if there is an error when loading the store with id, we just skip it when adding.
                            }
                        }
                        return null;
                    });
            return res;
        } catch (DatabaseException | ShoppingListException e) {
            throw new StoreException("Error when getting stores that contain all products from a shopping list ", e);
        }
    }

    @Override
    public ArrayList<Store> getStoresWithoutAllProductsFrom(ShoppingList shoppingList) throws StoreException {
        var db = Database.getInstance();
        ArrayList<Store> res = new ArrayList<>();

        try {
            int shoppingListId = ShoppingList.getDAO().getShoppingListIdFromName(shoppingList.getName());
            db.query(
                    "SELECT St.id, St.name from Store St where St.id not in (SELECT S.id FROM Store S WHERE NOT EXISTS (SELECT * FROM ProductShoppingList PSL WHERE PSL.shopping_list_id=? AND NOT EXISTS (SELECT * FROM StoreProduct SP WHERE SP.product_id = PSL.product_id AND SP.store_id = S.id )))",
                    List.of(shoppingListId), result -> {
                        while (result.next()) {
                            try {
                                res.add(getStore(result.getString(2)));
                            } catch (StoreException ignored) {
                                // if we can't get a store with id, we just don't add it to the list
                            }
                        }
                        return null;
                    });
            return res;
        } catch (DatabaseException | ShoppingListException e) {
            throw new StoreException(
                    "Error when getting stores that contain none of the products from a shopping list ", e);
        }
    }

    @Override
    public HashMap<Store, Float> getListOfPriceByStore(ShoppingList shoppingList) throws StoreException {
        List<Store> stores = getStoresWithAllProductsFrom(shoppingList);
        HashMap<Store, Float> res = new HashMap<>();
        var db = Database.getInstance();

        for (Store store : stores) {
            try {
                Float price = db.query(
                        "SELECT sum(price) FROM StoreProduct SP JOIN ProductShoppingList PSL ON PSL.product_id = SP.product_id WHERE SP.store_id = ? AND PSL.shopping_list_id = ? GROUP BY SP.store_id",
                        List.of(getIdFromStore(store),
                                ShoppingList.getDAO().getShoppingListIdFromName(shoppingList.getName())),
                        Statement.RETURN_GENERATED_KEYS, result -> result.next() ? result.getFloat(1) : null);
                res.put(store, price);
            } catch (DatabaseException | ShoppingListException e) {
                throw new StoreException("Error when getting a list of price by store ", e);
            }
        }
        return res;
    }

    @Override
    public boolean isProductInStore(Product product, Store store) throws StoreException {
        var db = Database.getInstance();

        try {
            var idProduct = Product.getDAO().getIdFromProduct(product);
            if (idProduct == null)
                return false;
            var idStore = getIdFromStore(store);
            return db.query("SELECT * FROM StoreProduct WHERE store_id = ? AND product_id = ?",
                    List.of(idStore, idProduct), ResultSet::next);
        } catch (DatabaseException | ProductException ignored) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }

    @Override
    public boolean isProductInStore(Product product, Store store, Float price) throws StoreException {
        var db = Database.getInstance();

        try {
            var idProduct = Product.getDAO().getIdFromProduct(product);
            var idStore = getIdFromStore(store);
            return db.query("SELECT * FROM StoreProduct WHERE store_id = ? AND product_id = ? AND price = ?",
                    List.of(idStore, idProduct, price), ResultSet::next);
        } catch (DatabaseException | ProductException e) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }
}
