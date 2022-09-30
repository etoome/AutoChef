package ulb.infof307.g07.database.dao.shoppinglist;

import org.apache.commons.lang3.reflect.FieldUtils;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.util.*;

public class ShoppingListMockDAO implements ShoppingListDAO {
    HashMap<Integer, ShoppingList> idToShoppingList;
    HashMap<Integer, Product> productIds;

    public ShoppingListMockDAO() {
        idToShoppingList = new HashMap<>();
        productIds = new HashMap<>();
        idToShoppingList.put(1, new ShoppingList("dummy1"));
        idToShoppingList.put(2, new ShoppingList("dummy2"));
        idToShoppingList.put(3, new ShoppingList("dummy3"));

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
            productIds.put(i + 1, new Product(names.get(i), Product.Category.BAKERY));
        }
    }

    @Override
    public List<ShoppingList> getAllShoppingLists() throws ShoppingListException {
        return new ArrayList<>(idToShoppingList.values());
    }

    @Override
    public List<ShoppingList> fillShoppingListsWithProducts(List<ShoppingList> shoppinglists)
            throws ShoppingListException {
        if (shoppinglists == null) {
            throw new ShoppingListException("null argument");
        }
        for (ShoppingList sl : shoppinglists) {
            try {
                for (Product pro : Product.getDAO().getAllProducts()) {
                    sl.addProduct(pro);
                }
            } catch (ProductException ignored) {
                // we skip this shopping list if we fail to load the products
            }
        }
        return shoppinglists;
    }

    @Override
    public ShoppingList fillShoppingListWithProducts(ShoppingList shoppinglist) throws ShoppingListException {
        if (shoppinglist == null) {
            return null;
        }
        try {
            for (Product pro : Product.getDAO().getAllProducts()) {
                shoppinglist.addProduct(pro);
            }
        } catch (ProductException e) {
            throw new ShoppingListException("Failed to fetch all products", e);
        }
        return shoppinglist;
    }

    @Override
    public String getProductNameFromProductShoppingListId(Integer id) throws ShoppingListException {
        if (id == null) {
            throw new ShoppingListException("can't search null id");
        }
        return productIds.get(id).getName();
    }

    @Override
    public int getProductPriceFromProductShoppingListId(Integer id) throws ShoppingListException {
        if (id == null) {
            throw new ShoppingListException("can't search null id");
        }
        return (int) productIds.get(id).getPrice();
    }

    @Override
    public ShoppingList createShoppingList(String name) {
        var res = new ShoppingList(name);
        idToShoppingList.put(idToShoppingList.size() + 1, res);
        return res;
    }

    @Override
    public boolean shoppingListExist(String name) {
        for (ShoppingList sl : idToShoppingList.values()) {
            if (Objects.equals(sl.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateShoppingListName(int id, String newName) {
        try {
            idToShoppingList.get(id).setName(newName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void setArchive(String name, boolean is_archived) throws ShoppingListException {
        for (ShoppingList sl : idToShoppingList.values()) {
            if (Objects.equals(sl.getName(), name)) {
                if (sl.isArchived() == is_archived) {
                    return;
                } else {
                    if (sl.isArchived()) {
                        try {
                            // uses reflection to set the archived field without creating an accessor
                            FieldUtils.writeField(sl, "archived", false, true);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            FieldUtils.writeField(sl, "archived", true, true);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        sl.archive();
                    }
                }
            }
        }
    }

    @Override
    public void deleteShoppingList(String shoppinglistName) {
        try {
            Integer res = getShoppingListIdFromName(shoppinglistName);
            idToShoppingList.remove(res);
        } catch (ShoppingListException ignored) {
        }

    }

    @Override
    public Integer getShoppingListIdFromName(String shoppinglistName) throws ShoppingListException {
        for (Map.Entry<Integer, ShoppingList> entry : idToShoppingList.entrySet()) {
            if (Objects.equals(entry.getValue().getName(), shoppinglistName)) {
                return entry.getKey();
            }
        }
        throw new ShoppingListException("there is no Shopping list with this name");
    }

    @Override
    public void save(ShoppingList shoppingList) throws ShoppingListException {
        if (shoppingList == null) {
            throw new ShoppingListException("can't save null list");
        }
        idToShoppingList.put(idToShoppingList.size() + 1, shoppingList);
    }
}
