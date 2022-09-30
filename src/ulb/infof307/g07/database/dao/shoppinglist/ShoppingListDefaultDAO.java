package ulb.infof307.g07.database.dao.shoppinglist;

import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.models.recipe.IngredientUnit;
import ulb.infof307.g07.models.recipe.Quantity;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListDefaultDAO implements ShoppingListDAO {

    @Override
    public List<ShoppingList> getAllShoppingLists() throws ShoppingListException {
        var db = Database.getInstance();
        List<ShoppingList> shoppingLists = new ArrayList<>();

        try {
            var success = db.query("SELECT * FROM ShoppingList;", result -> {
                while (result.next()) {
                    var shoppingList = new ShoppingList(result.getString("name"), result.getBoolean("is_archived"));
                    try {
                        fillShoppingListWithProducts(shoppingList);
                    } catch (ShoppingListException exception) {
                        return false;
                    }
                    shoppingLists.add(shoppingList);
                }
                return true;
            });
            if (!success) {
                throw new ShoppingListException("Error when getting all shopping lists");
            }
            return shoppingLists;
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when getting all shopping lists ", exception);
        }
    }

    @Override
    public List<ShoppingList> fillShoppingListsWithProducts(List<ShoppingList> shoppinglists)
            throws ShoppingListException {
        var db = Database.getInstance();

        try {
            for (ShoppingList shoppinglist : shoppinglists) {
                db.query(
                        "SELECT quantity, unit, P.name, P.category FROM ProductShoppingList AS PSL "
                                + "JOIN ShoppingList AS SL ON PSL.shopping_list_id = SL.id "
                                + "JOIN Product AS P ON PSL.product_id = P.id WHERE SL.name = ?;",
                        List.of(shoppinglist.getName()), result -> {
                            while (result.next()) {
                                var product = new Product(result.getString(3),
                                        Product.Category.valueOf(result.getString(4)));
                                shoppinglist.addProduct(product,
                                        new Quantity(result.getFloat(1), IngredientUnit.valueOf(result.getString(2))));
                            }
                            return null;
                        });
            }
            return shoppinglists;
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when filling shopping lists with products ", exception);
        }
    }

    @Override
    public ShoppingList fillShoppingListWithProducts(ShoppingList shoppinglist) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.query(
                    "SELECT quantity, unit, P.name, P.category FROM ProductShoppingList AS PSL JOIN ShoppingList AS SL ON PSL.shopping_list_id = SL.id JOIN Product AS P ON PSL.product_id = P.id WHERE SL.name = ?;",
                    List.of(shoppinglist.getName()), result -> {
                        while (result.next()) {
                            shoppinglist.addProduct(
                                    new Product(result.getString("name"),
                                            Product.Category.valueOf(result.getString("category"))),
                                    new Quantity(result.getFloat(1), IngredientUnit.valueOf(result.getString(2))));
                        }
                        return null;
                    });
            return shoppinglist;
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when filling shopping lists with products ", exception);
        }
    }

    @Override
    public String getProductNameFromProductShoppingListId(Integer id) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT name FROM Product WHERE id=?;", List.of(id),
                    result -> result.next() ? result.getString("name") : null);
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when getting product's name ", exception);
        }
    }

    @Override
    public int getProductPriceFromProductShoppingListId(Integer id) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT price FROM Product WHERE id=?;", List.of(id),
                    result -> result.next() ? result.getInt("price") : ShoppingList.Constants.NotAPrice.getValue());
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when getting product price ", exception);
        }
    }

    @Override
    public ShoppingList createShoppingList(String name) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.query("INSERT INTO ShoppingList(name, is_archived) VALUES(?,?);",
                    List.of(name, ShoppingList.ArchivedStatus.NotArchived.getValue()));
            return new ShoppingList(name);
        } catch (DatabaseException ignored) {
            throw new ShoppingListException("Shoppinglist name already taken ");
        }
    }

    @Override
    public boolean shoppingListExist(String name) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT 1 FROM ShoppingList WHERE name=?;", List.of(name), ResultSet::next);
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when checking if shopping list exist ", exception);
        }
    }

    @Override
    public boolean updateShoppingListName(int id, String newName) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.query("UPDATE ShoppingList SET name = ? WHERE id = ?", List.of(newName, id));
            return true;
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when updating shopping list's name ", exception);
        }
    }

    @Override
    public void setArchive(String name, boolean is_archived) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.query("UPDATE ShoppingList SET is_archived = ? WHERE name = ?",
                    List.of(is_archived ? ShoppingList.ArchivedStatus.Archived.getValue()
                            : ShoppingList.ArchivedStatus.NotArchived.getValue(), name));
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when archiving / unarchiving a shopping list ", exception);
        }
    }

    @Override
    public void deleteShoppingList(String shoppinglistName) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.begin();
            var id_sl = getShoppingListIdFromName(shoppinglistName);
            db.query("DELETE FROM ProductShoppingList WHERE shopping_list_id=?;", List.of(id_sl));
            db.query("DELETE FROM ShoppingList WHERE id=?;", List.of(id_sl));
            db.commit();
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when deleting a shopping list ", exception);
        }
    }

    @Override
    public Integer getShoppingListIdFromName(String shoppinglistName) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            return db.query("SELECT id FROM ShoppingList WHERE name=?", List.of(shoppinglistName), result -> {
                if (result.next())
                    return result.getInt(1);
                return ShoppingList.Constants.NotAnId.getValue();
            });
        } catch (DatabaseException exception) {
            throw new ShoppingListException("Error when getting a shopping list's id ", exception);
        }
    }

    @Override
    public void save(ShoppingList shoppingList) throws ShoppingListException {
        var db = Database.getInstance();

        try {
            db.begin();
            var id = getShoppingListIdFromName(shoppingList.getName());
            db.query("DELETE FROM ProductShoppingList WHERE shopping_list_id=?;", List.of(id));
            for (var cartproduct : shoppingList.getProducts().entrySet()) {
                db.query(
                        "INSERT INTO ProductShoppingList(product_id, shopping_list_id, quantity, unit) VALUES(?,?,?,?);",
                        List.of(Product.getDAO().getIdFromProduct(cartproduct.getKey()), id,
                                cartproduct.getValue().getQuantity(), cartproduct.getValue().getUnit().name()));
            }
            db.commit();
        } catch (DatabaseException | ProductException exception) {
            throw new ShoppingListException("Failed to save shopping list in database! ", exception);
        }
    }
}
