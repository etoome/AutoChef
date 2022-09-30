package ulb.infof307.g07.models.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.recipe.exceptions.QuantityException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestQuantity {
    ArrayList<Quantity> testQuantities;

    @BeforeEach
    void resetTestData() {
        testQuantities = new ArrayList<>();
        testQuantities.add(new Quantity(1, IngredientUnit.G));
        testQuantities.add(new Quantity(1, IngredientUnit.KG));
        testQuantities.add(new Quantity(1, IngredientUnit.L));
        testQuantities.add((new Quantity(1, IngredientUnit.ML)));
        testQuantities.add(new Quantity(1, IngredientUnit.UNIT));
    }

    @Test
    void testSetQuantityThrowsWithNegativeValue() {
        assertThrows(QuantityException.class, () -> testQuantities.get(0).setQuantity(-1));
    }

    @Test
    void testSetQuantityCorrect() {
        testQuantities.get(2).setQuantity(0.37f);
        assertEquals(0.37f, testQuantities.get(2).getQuantity());
    }

    @Test
    void testSetUnitThrowsWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> testQuantities.get(0).setUnit(null));
    }

    @Test
    void testSetUnitCorrect() {
        testQuantities.get(2).setUnit(IngredientUnit.UNIT);
        assertEquals(IngredientUnit.UNIT, testQuantities.get(2).getUnit());
    }

    @Test
    void testAddQuantityThrowsWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> testQuantities.get(0).add(null));
    }

    @Test
    void testAddWorksWithNormalCases() {
        testQuantities.get(0).add(testQuantities.get(1));
        assertEquals(2, testQuantities.get(0).getQuantity());
    }

    @Test
    void testSubQuantityThrowsWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> testQuantities.get(0).sub(null));
    }

    @Test
    void testSubWorksWithNormalCases() {
        testQuantities.get(0).sub(testQuantities.get(1));
        assertEquals(0, testQuantities.get(0).getQuantity());
    }

    @Test
    void testAddWorksWithValueConversions() {
        assertDoesNotThrow(() -> testQuantities.get(0).add(testQuantities.get(1)));
        assertDoesNotThrow(() -> testQuantities.get(2).add(testQuantities.get(3)));
        assertThrows(QuantityException.class, () -> testQuantities.get(0).add(testQuantities.get(3)));
    }

    @Test
    void testConvertToThrowsWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> testQuantities.get(0).convertTo(null));
    }
}