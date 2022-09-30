package ulb.infof307.g07.database.dao.product;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductDefaultDAO implements ProductDAO {

    @Override
    public Integer getIdFromProduct(Product product) throws ProductException {
        return getIdFromProduct(product.getName());
    }

    @Override
    public Integer getIdFromProduct(String name) throws ProductException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM Product WHERE name=?;", List.of(name),
                    result -> result.next() ? result.getInt("id") : null);
        } catch (DatabaseException e) {
            throw new ProductException("Error when getting id from product ", e);
        }
    }

    @Override
    public List<Product> getAllProducts() throws ProductException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT * FROM Product;", result -> {
                var products = new ArrayList<Product>();
                while (result.next()) {
                    products.add(new Product(result.getString("name"),
                            Product.Category.valueOf(result.getString("category"))));
                }
                return products;
            });
        } catch (DatabaseException e) {
            throw new ProductException("Error when getting all products ", e);
        }
    }

    @Override
    public boolean isProductAlreadyInDb(Product product) {
        var db = Database.getInstance();

        try {
            return db.query("SELECT * FROM Product WHERE name=?;", List.of(product.getName()), ResultSet::next);
        } catch (DatabaseException e) {
            return false; // if we change this to throw an error, we need to do a try catch in our if(check)
        }
    }

    @Override
    public boolean createProduct(Product product) throws ProductException {
        var db = Database.getInstance();

        try {
            if (isProductAlreadyInDb(product))
                return false;
            db.query("INSERT INTO Product(name, category) VALUES(?, ?);",
                    List.of(product.getName(), product.getCategory()));
            return true;
        } catch (DatabaseException e) {
            throw new ProductException("Error when getting all products ", e);
        }
    }

    @Override
    public Product getProduct(String name) throws ProductException {
        var db = Database.getInstance();
        var idProduct = getIdFromProduct(name);

        try {
            return db.query("SELECT * FROM Product WHERE id=?;", List.of(idProduct), result -> result.next()
                    ? new Product(result.getString("name"), Product.Category.valueOf(result.getString("category")))
                    : null);
        } catch (DatabaseException e) {
            throw new ProductException("Error when getting product ", e);
        }
    }
}
