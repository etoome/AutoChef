package ulb.infof307.g07.models.store;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.store.StoreDAO;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.util.ArrayList;
import java.util.List;

public class Store {
    private final Position position;
    private final String name;

    private final ArrayList<Product> products = new ArrayList<>();

    public Store(String name, Position position) {
        if (name == null) {
            throw new IllegalArgumentException("Can't make a store with null name");
        }
        if (position == null) {
            throw new IllegalArgumentException("Can't make a store with null position");
        }
        this.position = position;
        this.name = name;
    }

    public Store(String name, double lat, double lng) {
        if (name == null) {
            throw new IllegalArgumentException("Can't make a store with no name");
        }
        this.position = new Position(lat, lng);
        this.name = name;
    }

    public Position getPos() {
        return position;
    }

    public double getLat() {
        return position.lat();
    }

    public double getLng() {
        return position.lng();
    }

    public String getName() {
        return name;
    }

    /** Get the absolute distance between two stores */
    public double distance(Store other) {
        return other.position.distanceAbsolute(position);
    }

    /** Get the absolute distance between the store and a position */
    public double distance(Position other) {
        return other.distanceAbsolute(position);
    }

    /**
     * Method that adds product to a Store
     *
     * @param product
     *            : the product to add
     *
     * @throws StoreException
     *             : throws an exception if the product to add is null
     */
    public void addProduct(Product product) throws StoreException {
        if (product == null) {
            throw new StoreException("can't add null products");
        }
        if (!products.contains(product)) {
            products.add(product);
        }
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    /** True if the store has this particular product */
    public boolean hasProduct(Product product) {
        return products.contains(product);
    }

    public static StoreDAO getDAO() {
        return DAO.getInstance().getStoreDAO();
    }

    /**
     * Method that returns the cheapest Store , with all the products of a given shopping list
     *
     * @param shoppingList
     *            : the shopping list
     *
     * @return : the cheapest store
     *
     * @throws StoreException
     *             : throws an exception error if error while querying the db
     */
    public static Store getCheapestStore(ShoppingList shoppingList) throws StoreException {
        Store currentBest = null;
        float currentMin = 1000000000;
        var listStores = getDAO().getListOfPriceByStore(shoppingList);
        for (var store : listStores.entrySet()) {
            if (store.getValue() < currentMin) {
                currentBest = store.getKey();
                currentMin = store.getValue();
            }
        }
        return currentBest;

    }

    /**
     * Method that returns the closest Store from Home, with all the products of a given shopping list
     *
     * @param shoppingList
     *            : the shopping list
     * @param home
     *            : the home
     *
     * @return : the closest store
     *
     * @throws StoreException
     *             : throws an exception error if error while querying the db
     */
    public static Store getClosestStore(ShoppingList shoppingList, Home home) throws StoreException {
        List<Store> stores = getDAO().getStoresWithAllProductsFrom(shoppingList);
        double bestDist = 10000000.0;
        double lat;
        double lng;
        Store currentBestStore = null;
        for (Store store : stores) {
            lat = store.getLat() - home.getLat();
            lng = store.getLng() - home.getLng();
            double dist = Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));
            if (dist < bestDist) {
                bestDist = dist;
                currentBestStore = store;
            }

        }
        return currentBestStore;
    }

    /**
     * Method that gets all the stores
     *
     * @return list containing all the stores
     */
    public static ArrayList<Store> getAllStores() throws StoreException {
        return getDAO().getAllStores();
    }

    /**
     * @return stores without everything from a shoppingList
     */
    public static ArrayList<Store> getStoresWithoutFrom(ShoppingList shoppingList) throws StoreException {
        return getDAO().getStoresWithoutAllProductsFrom(shoppingList);
    }

    /**
     * Method that checks if 2 stores are the same
     *
     * @return boolean: - true if the same - false else
     */
    public boolean isEqual(Store store) {
        return this.getName().equals(store.getName());
    }

}
