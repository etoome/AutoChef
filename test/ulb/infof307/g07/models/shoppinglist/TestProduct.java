package ulb.infof307.g07.models.shoppinglist;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.database.dao.product.ProductDAO;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.utils.Config;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestProduct {
    static ProductDAO dao;
    static ArrayList<String> names = new ArrayList<>();

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
        dao = Product.getDAO();
    }

    @BeforeAll
    static void setNames() {
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
    }

    @Test
    void testProductConstructionIntegrity() {
        var product1 = new Product("product1", Product.Category.BAKERY);
        var product2 = new Product("product2", Product.Category.BAKERY, 13);
        assertEquals("product1", product1.getName());
        assertEquals(0, product1.getPrice());
        assertEquals("product2", product2.getName());
        assertEquals(13, product2.getPrice());
    }

    @Test
    void testProductConstructionWithNullNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Product(null, Product.Category.BAKERY));
        assertThrows(IllegalArgumentException.class, () -> new Product(null, Product.Category.BAKERY, 1));
    }

    @Test
    void testProductConstructionWithNullCategoryShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Product("some name", null));
        assertThrows(IllegalArgumentException.class, () -> new Product("some name", null, 1));
    }

    @Test
    void testProductConstructionWithNegativePriceThrows() {
        assertThrows(IllegalArgumentException.class, () -> new Product("product", Product.Category.BAKERY, -1));
    }

    @Test
    void testEqualsWithHimselfReturnTrue() {
        var product1 = new Product("some name", Product.Category.SAUCE);
        // noinspection UnnecessaryLocalVariable
        var product2 = product1;
        assertEquals(product1, product2);
    }

    @Test
    void testEqualsWithNotAProductReturnFalse() {
        var obj = new Object();
        var product = new Product("some name", Product.Category.SAUCE);
        assertNotEquals(product, obj);
    }

    @Test
    void testGetIdFromProductNameWorksNormalCase() throws ProductException {
        for (int i = 0; i < names.size(); i++) {
            assertEquals(i + 1, dao.getIdFromProduct(names.get(i)));
        }
    }

    @Test
    void testGetIdFromProductWorksNormalCase() throws ProductException {
        for (int i = 0; i < names.size(); i++) {
            assertEquals(i + 1, dao.getIdFromProduct(new Product(names.get(i), Product.Category.BAKERY)));
        }
    }

    @Test
    void testGetAllProductsWorks() throws ProductException {
        var res = dao.getAllProducts();
        assertEquals(names.size(), res.size());
        for (String name : names) {
            assertTrue(res.contains(new Product(name, Product.Category.BAKERY)));
        }
    }

    @Test
    void testCreateValueCategoryCorrect() {
        Product.Category categoryTest = Product.Category.CARB;
        assertEquals(Product.Category.CARB.getValue(), categoryTest.getValue());
    }

    @Test
    void testGetValueStaticCategoryCorrect() {
        assertEquals(Product.Category.SPICE, Product.Category.getValue("Epices"));
    }

    @Test
    void testGetValueStaticCategoryWithNullParamShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> {
            Product.Category.getValue(null);
        });
    }
}
