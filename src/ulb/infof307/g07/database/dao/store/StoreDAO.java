package ulb.infof307.g07.database.dao.store;

import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.util.ArrayList;
import java.util.HashMap;

public interface StoreDAO {
    boolean isStoreAlreadyInDb(Store store) throws StoreException;

    Integer getIdFromStore(Store store) throws StoreException;

    /**
     * Method that creates a store in the DB with an object Store
     *
     * @param store:
     *            a store
     *
     * @return if the store is created => true else (=> false): there was an error or the store's name is already taken
     *         in the DB
     */
    boolean createStore(Store store) throws StoreException;

    /**
     * Method that deletes the given store
     *
     * @param store
     *            : the given store
     *
     * @throws StoreException:
     *             throw an exception error if an error occured when queryong the db
     */
    void deleteStore(Store store) throws StoreException;

    /**
     * Method that gets all stores in the DB
     *
     * @return Arraylist containing all stores
     */
    ArrayList<Store> getAllStores() throws StoreException;

    Store getStore(String name) throws StoreException;

    /**
     * Method that gets all products stored in a store
     *
     * @param name:
     *            name of the store
     *
     * @return Arraylist containing all products in the store
     */
    ArrayList<Product> getProductsInStore(String name) throws StoreException;

    /**
     * Method that adds a product to a given store
     *
     * @param product
     *            : the product to add
     * @param store
     *            : the store in which we want to add the product
     *
     * @throws StoreException
     *             : throws an exception error when an error occurs when querrying the db
     */
    void addProductInStore(Product product, Store store) throws StoreException;

    /**
     * Method that returns the stores containing all the products of a given shopping list
     *
     * @param shoppingList
     *            : the given shopping list
     *
     * @return list of the stores
     */
    ArrayList<Store> getStoresWithAllProductsFrom(ShoppingList shoppingList) throws StoreException;

    /**
     * Method that returns the store that do not contain all the product of the shoppinglist
     */
    ArrayList<Store> getStoresWithoutAllProductsFrom(ShoppingList shoppingList) throws StoreException;

    /**
     * Method that returns the price of a given shopping list, by store, if the store has all the products of the
     * shopping list
     *
     * @param shoppingList
     *            : the given shopping list
     *
     * @return dictionnary with the price for each store
     *
     * @throws StoreException:
     *             throws an exception error when an error occured querrying the dn
     */
    HashMap<Store, Float> getListOfPriceByStore(ShoppingList shoppingList) throws StoreException;

    boolean isProductInStore(Product product, Store store) throws StoreException;

    boolean isProductInStore(Product product, Store store, Float price) throws StoreException;
}
