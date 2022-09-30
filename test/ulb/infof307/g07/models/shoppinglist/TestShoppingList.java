package ulb.infof307.g07.models.shoppinglist;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.recipe.IngredientUnit;
import ulb.infof307.g07.models.recipe.Quantity;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;
import ulb.infof307.g07.utils.Config;

import static org.junit.jupiter.api.Assertions.*;

public class TestShoppingList {

    ShoppingList shoppinglist;
    Product dummyProduct1 = new Product("DummyProduct1", Product.Category.BAKERY, 1);
    Product dummyProduct2 = new Product("DummyProduct2", Product.Category.BAKERY, 2);
    Product dummyProduct3 = new Product("DummyProduct3", Product.Category.BAKERY, 3);

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
    }

    @BeforeEach
    void initShoppingList() {
        shoppinglist = new ShoppingList("List");
    }

    @Test
    void testConstructorWorksAsIntended() {
        var listName = "some name";
        var archiveStatus = true;
        var shoppingList = new ShoppingList(listName, archiveStatus);
        assertEquals(listName, shoppingList.getName());
        assertEquals(archiveStatus, shoppingList.isArchived());
    }

    @Test
    void testConstructorWithNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new ShoppingList(null));
        assertThrows(IllegalArgumentException.class, () -> new ShoppingList(null, true));
    }

    @Test
    void testSetNameWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new ShoppingList("some name").setName(null));
    }

    @Test
    void testUpdateNameWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new ShoppingList("some name").updateName(null));
    }

    @Test
    void testArchivingWorks() throws ShoppingListException {
        var shoppingList = new ShoppingList("some name");
        shoppingList.archive();
        assertTrue(shoppingList.isArchived());
        shoppingList.unarchive();
        assertFalse(shoppingList.isArchived());
    }

    @Test
    void testGetCartProducts() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertFalse(shoppinglist.hasProduct(dummyProduct2));
        shoppinglist.addProduct(dummyProduct2);
        assertTrue(shoppinglist.hasProduct(dummyProduct2));
        assertFalse(shoppinglist.hasProduct(dummyProduct3));
        shoppinglist.addProduct(dummyProduct3);
        assertTrue(shoppinglist.hasProduct(dummyProduct3));
        shoppinglist.setProductQuantity(dummyProduct1, 3);
        shoppinglist.setProductQuantity(dummyProduct2, 4);
        shoppinglist.setProductQuantity(dummyProduct3, 2);
        var products = shoppinglist.getProducts();
        int product_count = 0;
        for (var product : products.entrySet()) {
            if (product.getKey() == dummyProduct1) {
                assertEquals(3, product.getValue().getQuantity());
                ++product_count;
            } else if (product.getKey() == dummyProduct2) {
                assertEquals(4, product.getValue().getQuantity());
                ++product_count;
            } else if (product.getKey() == dummyProduct3) {
                assertEquals(2, product.getValue().getQuantity());
                ++product_count;
            }
        }
        assertEquals(3, product_count);
    }

    @Test
    void testAddProductWorks() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertEquals(1F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testAddProductWithFloatQuantity() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1, 3F);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertEquals(3F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testAddProductWithObjQuantity() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1, new Quantity(2, IngredientUnit.UNIT));
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertEquals(2F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testAddAlreadyPresentProductIncreaseValue() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertEquals(2F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testAddNewQuantityToProductWithQuantityObj() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1, new Quantity(3, IngredientUnit.UNIT));
        assertEquals(4F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testAddNewQuantityToProductWithFloat() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1, 3F);
        assertEquals(4F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testCleanWorks() {
        assertTrue(shoppinglist.isEmpty());
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct2);
        shoppinglist.addProduct(dummyProduct3);
        assertFalse(shoppinglist.isEmpty());
        shoppinglist.clean();
        assertTrue(shoppinglist.isEmpty());
    }

    @Test
    void testRemoveProductWorks() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.removeProduct(dummyProduct1);
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
    }

    @Test
    void testRemoveNonPresentProductThrows() {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        assertThrows(ShoppingListException.class, () -> shoppinglist.removeProduct(dummyProduct1));
    }

    @Test
    void testIncrementProduct() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        float quantity = shoppinglist.getProductQuantityValue(dummyProduct1);
        shoppinglist.setProductQuantity(dummyProduct1, quantity + 1);
        assertEquals(2F, shoppinglist.getProductQuantityValue(dummyProduct1));

    }

    @Test
    void testDecrementProduct() throws ShoppingListException {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.setProductQuantity(dummyProduct1, 2);
        float quantity = shoppinglist.getProductQuantityValue(dummyProduct1);
        shoppinglist.setProductQuantity(dummyProduct1, quantity - 1);
        assertEquals(1F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testSubstractQuantityFromProductWithQuantityObj() throws ShoppingListException {
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.substractProduct(dummyProduct1, new Quantity(2, IngredientUnit.UNIT));
        assertEquals(2F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testSubstractQuantityFromProductWithFloat() throws ShoppingListException {
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.substractProduct(dummyProduct1, 2F);
        assertEquals(2F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testSubstractQuantityFromProductWithQuantityObjButIncorrectCatShouldDoNothing() throws ShoppingListException {
        shoppinglist.addProduct(dummyProduct1); // by default unit = UNIT
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.addProduct(dummyProduct1);
        shoppinglist.substractProduct(dummyProduct1, new Quantity(2, IngredientUnit.KG));
        assertEquals(4F, shoppinglist.getProductQuantityValue(dummyProduct1));
    }

    @Test
    void testSetProductQuantityToZeroThrows() {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertThrows(ShoppingListException.class, () -> shoppinglist.setProductQuantity(dummyProduct1, 0));
    }

    @Test
    void testSetProductQuantityToBelowZeroThrows() {
        assertFalse(shoppinglist.hasProduct(dummyProduct1));
        shoppinglist.addProduct(dummyProduct1);
        assertTrue(shoppinglist.hasProduct(dummyProduct1));
        assertThrows(ShoppingListException.class, () -> shoppinglist.setProductQuantity(dummyProduct1, -5));
    }
}
