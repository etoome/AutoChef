package ulb.infof307.g07.models.map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.store.Position;
import ulb.infof307.g07.utils.Config;

import static org.junit.jupiter.api.Assertions.*;

class TestPosition {
    Position testPos;

    @BeforeAll
    public static void setConfigEnv() {
        if (!Config.isPreconfiguredEnvironmentEqualTo(Config.Environment.TESTING)) {
            Config.setEnvironment(Config.Environment.TESTING);
        }
        var currentEnv = Config.getInstance().getEnvironment();
        assert currentEnv == Config.Environment.TESTING;
    }

    @BeforeEach
    void setData() {
        testPos = new Position(1, 1);
    }

    @Test
    void testEqualsWorksInNormalCase() {
        assertTrue(testPos.equals(new Position(1, 1)));

    }

    @Test
    void testEqualsReturnsFalseWithNull() {
        assertFalse(testPos.equals(null));
    }

    @Test
    void testDistanceAbsoluteWorksNormalCase() {
        assertEquals(10, testPos.distanceAbsolute(new Position(11, 1)));
    }

    @Test
    void testDistanceAbsoluteNullArgShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testPos.distanceAbsolute(null));
    }

    @Test
    void testRandomPositionBetweenWorks() {
        for (int i = 0; i < 100; i++) {
            var tested = Position.generateRandomPositionBetween(0, 1000, 0, 1000);
            assertTrue(tested.lat() > 0 && tested.lat() < 1000 && tested.lng() > 0 && tested.lng() < 1000);
        }
    }

    @Test
    void testRandomPositionBetweenWithWrongRangeThrows() {
        assertThrows(IllegalArgumentException.class, () -> Position.generateRandomPositionBetween(1000, 0, 1, 0));
    }
}
