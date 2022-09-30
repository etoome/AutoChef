package ulb.infof307.g07.models.recipe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.database.dao.ingredient.IngredientDAO;
import ulb.infof307.g07.models.recipe.exceptions.IngredientException;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.utils.Config;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestIngredient {

    Ingredient testIngredient;
    static IngredientDAO dao;
    ArrayList<String> names = new ArrayList<>();
    Quantity testQuantity = new Quantity(1.f, IngredientUnit.UNIT);

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
        dao = Ingredient.getDAO();
    }

    @BeforeEach
    void resetTestData() {
        names.add("mayonnaise");
        names.add("sauce");
        names.add("olive");
        names.add("soupe");
        names.add("crouton");
        names.add("mouton");
        names.add("boeuf");
        names.add("mousse");
        names.add("creme brulee");
        names.add("mousse");
        testIngredient = new Ingredient(names.get(1), testQuantity);
    }

    @Test
    void testIngredientCreatedSuccessfully() {
        var ingredient = new Ingredient("some name", 3f, IngredientUnit.UNIT);
        assertEquals("some name", ingredient.getName());
        assertEquals(3f, ingredient.getQuantityValue());
        assertEquals(IngredientUnit.UNIT, ingredient.getUnit());
    }

    @Test
    void testIngredientConstructorWithNullNameShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient(null, 1f, IngredientUnit.UNIT));
    }

    @Test
    void testIngredientWithNegQuantityShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient("Some name", -1f, IngredientUnit.UNIT));
    }

    @Test
    void testIngredientWithNullUnitShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Ingredient("some name", 1f, null));
    }

    @Test
    void testSetNameWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testIngredient.setName(null));
    }

    @Test
    void testSetNameCorrect() {
        testIngredient.setName("poire");
        assertEquals("poire", testIngredient.getName());
    }

    @Test
    void testSetQuantityCorrect() {
        testIngredient.setQuantity(37.f);
        assertEquals(37.f, testIngredient.getQuantity().getQuantity());
        assertEquals(37.f, testIngredient.getQuantityValue());
    }

    @Test
    void testSetUnitWithNullShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testIngredient.setUnit(null));
    }

    @Test
    void testSetIngredientCorrect() {
        testIngredient.setUnit(IngredientUnit.KG);
        assertEquals(IngredientUnit.KG, testIngredient.getUnit());
    }

    @Test
    void testgetProductFromIngredientIDWorksNormalCase() throws IngredientException {
        for (int i = 0; i < 10; i++) {
            var res = dao.getProductFromIngredient(i + 1);
            assertEquals(new Product(names.get(i), Product.Category.BAKERY).getName(), res.getName());
        }

    }

    @Test
    void testGetProductFromIngredientIDReturnsNullOnInvalidID() throws IngredientException {
        var res = dao.getProductFromIngredient(11);
        assertNull(res);
    }

    @Test
    void testgetProductFromIngredientNameWorksNormalCase() throws IngredientException {

        var res = dao.getProductFromIngredient("mayonnaise");
        assertEquals(new Product("mayonnaise", Product.Category.SAUCE).getName(), res.getName());
    }

    @Test
    void testGetProductFromIngredientIDReturnsNullOnInvalidName() throws IngredientException {
        var res = dao.getProductFromIngredient(11);
        assertNull(res);
    }

    @Test
    void testGetIngredientIdFromNameWorksNormalCase() throws IngredientException {
        var res = dao.getIngredientIdFromName("mayonnaise");
        assertEquals(res, 1);
    }

}
