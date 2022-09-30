package ulb.infof307.g07.models.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ulb.infof307.g07.models.agenda.exceptions.DateRangeException;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TestDateRange {
    public DateRange testDateRange;
    public LocalDate testBeginDate = LocalDate.of(2030, 1, 1);
    public LocalDate testInRangeDate = LocalDate.of(2030, 1, 4);
    public LocalDate testEndDate = LocalDate.of(2030, 1, 8);

    @BeforeEach
    public void resetTestData() {
        testDateRange = new DateRange(testBeginDate, testEndDate);
    }

    @Test
    public void testInstantiateObjectWithImpossibleRangeShouldThrow() {
        assertThrows(DateRangeException.class, () -> testDateRange = new DateRange(testEndDate, testBeginDate));
    }

    @Test
    public void testGetDaysIsCorrect() {
        ArrayList<LocalDate> testWeek = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            testWeek.add(LocalDate.of(2030, 1, i));
        }
        assertEquals(testWeek, testDateRange.getDays());
    }

    @Test
    public void testDaysUntilIsCorrect() {
        ArrayList<LocalDate> testWeek = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            testWeek.add(LocalDate.of(2030, 1, i));
        }
        assertEquals(testWeek, DateRange.getDateUntil(testBeginDate, testEndDate));
    }

    @Test
    public void testGetLengthWorks() {
        assertEquals(8, testDateRange.getLength());
    }

    @Test
    public void testAssertDateIsInDateRangeWorks() {
        assertDoesNotThrow(() -> testDateRange.assertDateIsInDateRange(testInRangeDate));
    }

    @Test
    public void testAssertDateIsInDateRangeNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> testDateRange.assertDateIsInDateRange(null));
    }

    @Test
    public void testAssertDateIsInDateRangeDateBeforeBeginShouldThrow() {
        assertThrows(DateRangeException.class, () -> testDateRange.assertDateIsInDateRange(testBeginDate.minusDays(5)));
    }

    @Test
    public void testAssertDateIsInDateRangeDateAfterEndShouldThrow() {
        assertThrows(DateRangeException.class, () -> testDateRange.assertDateIsInDateRange(testEndDate.plusDays(5)));
    }

    @Test
    public void testGetDateUntilNullStartDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> DateRange.getDateUntil(null, testEndDate));
    }

    @Test
    public void testGetDateUntilNullStopDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> DateRange.getDateUntil(testBeginDate, null));
    }

    @Test
    public void testGetDateStringNullDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> DateRange.getDateString(null));
    }

    @Test
    public void testGetDateFromStringNullStringDateShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> DateRange.getDateFromString(null));
    }
}