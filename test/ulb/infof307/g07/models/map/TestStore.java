package ulb.infof307.g07.models.map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.store.Position;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;
import ulb.infof307.g07.utils.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestStore {
    Store testStore;
    Product tarte = new Product("tarte", Product.Category.BAKERY);

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
    }

    @BeforeEach
    void setUp() {
        testStore = new Store("carouf", 10, 10);
    }

    @Test
    void testConstructor() {
        var someStore = new Store("carouf", 10, 10);
        assertEquals("carouf", someStore.getName());
        assertEquals(10, someStore.getLat());
        assertEquals(10, someStore.getLng());
        var someOtherStore = new Store("WorstBuy", new Position(1, 1));
        assertEquals("WorstBuy", someOtherStore.getName());
        assertEquals(1, someOtherStore.getLat());
        assertEquals(1, someOtherStore.getLng());
    }

    @Test
    void testConstructorsThrowsWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Store(null, new Position(0, 0)));
        assertThrows(IllegalArgumentException.class, () -> new Store(null, 0, 0));
    }

    @Test
    void testConstructorWithNullPositionShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Store("some name", null));
    }

    @Test
    void testAddProductWorksNormalCase() {
        assertDoesNotThrow(() -> testStore.addProduct(tarte));
        ArrayList<Product> prod = new ArrayList<>(List.of(tarte));
        assertEquals(prod, testStore.getProducts());
    }

    @Test
    void testAddProductsWithNullProductThrows() {
        assertThrows(StoreException.class, () -> testStore.addProduct(null));
    }

    @Test
    void testContainsProductWorksWithAndWithoutProduct() {
        assertFalse(testStore.hasProduct(tarte));
        assertDoesNotThrow(() -> testStore.addProduct(tarte));
        assertTrue(testStore.hasProduct(tarte));
    }

    @Test
    void testDistanceWithStore() {
        var store1 = new Store("some name", 0, 0);
        var store2 = new Store("some other name", 0, 1);
        assertEquals(1, store1.distance(store2));
    }

    @Test
    void testDistanceWithPosition() {
        var store1 = new Store("some name", 0, 0);
        var store2 = new Store("some other name", 0, 1);
        assertEquals(1, store1.distance(store2.getPos()));
    }

    @Test
    void testIsEqual() {
        var sameName = "what a great name for a cool store";
        var otherName = "this is not such a cool name for a store";
        var store1 = new Store(sameName, 0, 0);
        var store2 = new Store(sameName, 1, 1);
        var store3 = new Store(otherName, 2, 2);
        assertTrue(store1.isEqual(store2));
        assertFalse(store1.isEqual(store3));
    }
}