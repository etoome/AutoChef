package ulb.infof307.g07.database.dao.product;

import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;

import java.util.List;

public interface ProductDAO {

    Integer getIdFromProduct(Product product) throws ProductException;

    Integer getIdFromProduct(String name) throws ProductException;

    /**
     * method that gets all products in the DB
     *
     * @return Arraylist containing all products
     */
    List<Product> getAllProducts() throws ProductException;

    boolean isProductAlreadyInDb(Product product);

    /**
     * Method who create a product in the DB with an object Product
     *
     * @param product:
     *            a product
     *
     * @return if the product is created => true else (=> false): there was an error or the product's name is already
     *         taken in the DB
     */
    boolean createProduct(Product product) throws ProductException;

    Product getProduct(String name) throws ProductException;
}
