package ulb.infof307.g07.models.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestInstruction {

    Instruction testInstruction;
    int key = 2;
    String value = "Cuire steak avec le fromage";

    @BeforeEach
    void resetTestData() {
        testInstruction = new Instruction(key, value);
    }

    @Test
    void testInstructionConstructorNullValueShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Instruction(key, null));
    }

    @Test
    void testInstructionCreateSuccessfully() {
        assertEquals(2, testInstruction.getKey());
        assertEquals("Cuire steak avec le fromage", testInstruction.getValue());
    }

    @Test
    void testSetNullValueShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testInstruction.setValue(null));
    }

    @Test
    void testSetValueCorrect() {
        testInstruction.setValue("Griller pain");
        assertEquals("Griller pain", testInstruction.getValue());
    }

    @Test
    void testSetKeyCorrect() {
        testInstruction.setKey(2);
        assertEquals(2, testInstruction.getKey());
    }

}