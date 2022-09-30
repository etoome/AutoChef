package ulb.infof307.g07.models.shoppinglist;

import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.shoppinglist.ShoppingListDAO;
import ulb.infof307.g07.models.recipe.IngredientUnit;
import ulb.infof307.g07.models.recipe.Quantity;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;

import java.util.HashMap;
import java.util.Map;

/** A collection of products with an archived status */
public class ShoppingList {

    private String name;
    private boolean archived;

    private final Map<Product, Quantity> products = new HashMap<>();

    public ShoppingList(String name) {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        this.name = name;
        this.archived = false;
    }

    public ShoppingList(String name, boolean archived) {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        this.name = name;
        this.archived = archived;
    }

    public String getName() {
        return name;
    }

    /** Sets the name, but doesn't update the persistance */
    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        this.name = name;
    }

    /** Sets the name and update the persistance */
    public void updateName(String name) throws ShoppingListException {
        if (name == null)
            throw new IllegalArgumentException("Empty name");
        getDAO().updateShoppingListName(getDAO().getShoppingListIdFromName(this.name), name);
        this.name = name;
    }

    public boolean isArchived() {
        return archived;
    }

    /** archive the shoppinglist, affects persistence */
    public void archive() throws ShoppingListException {
        getDAO().setArchive(this.name, true);
        this.archived = true;
    }

    /** unarchive the shoppinglist, affects persistence */
    public void unarchive() throws ShoppingListException {
        getDAO().setArchive(this.name, false);
        this.archived = false;
    }

    public boolean hasProduct(Product product) {
        for (Product productInShoppingList : this.products.keySet())
            if (product.equals(productInShoppingList))
                return true;
        return false;
    }

    /**
     * Query all products in a shopping list
     *
     * @return all products and their quantity in a shoppinglist
     */
    public HashMap<Product, Quantity> getProducts() {
        return new HashMap<>(products);
    }

    /**
     * Add product in shoppingList, the default IngredientUnit is UNIT, if you want to specify a unit you must use the
     * overloaded method with (Product, Quantity)
     *
     * @param product
     *            Product to add in shoppingList
     */
    public void addProduct(Product product) {
        addProduct(product, new Quantity(1, IngredientUnit.UNIT));
    }

    public void addProduct(Product product, Quantity quantity) {
        Product productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList != null) {
            Quantity newQuantity = this.products.get(productInShoppingList);
            if (IngredientUnit.sameUnitRange(newQuantity.getUnit(), quantity.getUnit())) {
                newQuantity.add(quantity);
                this.products.put(productInShoppingList, newQuantity);
                return;
            }
        }
        this.products.put(product, quantity);
    }

    public void addProduct(Product product, Float quantity) {
        Product productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList != null) {
            Quantity newQuantity = this.products.get(productInShoppingList);
            newQuantity.add(new Quantity(quantity, newQuantity.getUnit()));
        } else {
            this.products.put(product, new Quantity(quantity, IngredientUnit.UNIT));
        }
    }

    public void clean() {
        this.products.clear();
    }

    /**
     * If the product is in the shoppingList
     *
     * @param product
     *            Product to check if is in shoppingList
     */
    public Product getProductInShoppingList(Product product) {
        for (var productInShoppingList : this.products.keySet()) {
            if (productInShoppingList.equals(product))
                return productInShoppingList;
        }
        return null;
    }

    public void removeProduct(Product product) throws ShoppingListException {
        Product productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList == null) {
            throw new ShoppingListException("Cannot remove a product that is not in the shoppinglist");
        }
        this.products.remove(productInShoppingList);
    }

    public void substractProduct(Product product, Quantity quantity) {
        Product productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList != null) {
            Quantity newQuantity = this.products.get(productInShoppingList);
            if (IngredientUnit.sameUnitRange(newQuantity.getUnit(), quantity.getUnit())) {
                newQuantity.sub(quantity);
                this.products.put(productInShoppingList, newQuantity);
            }
        } // if product (with same unit) is not in the shopping list we have nothing to
          // substract
    }

    public void substractProduct(Product product, float quantity) {
        Product productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList != null)
            substractProduct(product, new Quantity(quantity, this.products.get(productInShoppingList).getUnit()));
        // if product (with same unit) is not in the shopping list we have nothing to
        // substract
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public Quantity getProductQuantity(Product product) throws ShoppingListException {
        var productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList == null) {
            throw new ShoppingListException("Product doesn't exist in shoppinglist");
        }
        return this.products.get(productInShoppingList);
    }

    public float getProductQuantityValue(Product product) throws ShoppingListException {
        return getProductQuantity(product).getQuantity();
    }

    public void setProductQuantity(Product product, Quantity quantity) throws ShoppingListException {
        if (quantity.getQuantity() <= 0) {
            throw new ShoppingListException("Cannot set quantity to 0 or bellow");
        }
        if (getProductInShoppingList(product) == null) {
            addProduct(product, quantity);
            return;
        }
        if (hasProduct(product))
            removeProduct(product); // We remove it from the list to avoid duplicate (Java bug idk)
        this.products.put(product, quantity);
    }

    public void setProductQuantity(Product product, float quantity) throws ShoppingListException {
        var productInShoppingList = getProductInShoppingList(product);
        if (productInShoppingList == null) {
            setProductQuantity(product, new Quantity(quantity, IngredientUnit.UNIT));
        } else {
            var productQuantityUnit = this.products.get(productInShoppingList).getUnit();
            setProductQuantity(product, new Quantity(quantity, productQuantityUnit));
        }
    }

    public static ShoppingListDAO getDAO() {
        return DAO.getInstance().getShoppingListDAO();
    }

    public enum ArchivedStatus {
        NotArchived(0), Archived(1);

        private final int value;

        ArchivedStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Constants {
        NotAPrice(-1), NotAnId(-1);

        private final Integer value;

        Constants(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    @Override
    public String toString() {
        return (this.archived ? "📦 " : "") + this.name;
    }
}
