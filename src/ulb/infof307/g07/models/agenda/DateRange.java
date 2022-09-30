package ulb.infof307.g07.models.agenda;

import javafx.util.StringConverter;
import ulb.infof307.g07.models.agenda.exceptions.DateRangeException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle dates system for the Agenda, get a period of time, get a week, or date format
 */
public final class DateRange {

    private final LocalDate beginning;
    private final LocalDate end;

    public static final int DAYS_PER_WEEK = 7;
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructor who initialize the DateRange object, with the beginning date & ending date
     */
    public DateRange(LocalDate beginning, LocalDate end) throws DateRangeException {
        if (beginning.isAfter(end)) {
            throw new DateRangeException("You can't create a Date range with an end date before the beginning");
        }
        this.beginning = beginning;
        this.end = end;
    }

    public LocalDate getBeginningDate() {
        return beginning;
    }

    public LocalDate getEndDate() {
        return end;
    }

    /**
     * @return an arraylist of all days in the range of begging & ending date include
     */
    public List<LocalDate> getDays() {
        List<LocalDate> days = beginning.datesUntil(end).collect(Collectors.toList());
        days.add(end);
        return days;
    }

    /**
     * @return get all days from the beginning & the ending date, including monday of the beginning & sunday of the
     *         ending date
     */
    public List<LocalDate> getDaysWeek() {
        return beginning.with(DayOfWeek.MONDAY).datesUntil(end.with(DayOfWeek.SUNDAY).plusDays(1)).toList();
    }

    /**
     * @return the actual week from monday to sunday
     */
    public static List<LocalDate> getActualWeek() {
        LocalDate now = LocalDate.now();
        return now.with(DayOfWeek.MONDAY).datesUntil(now.with(DayOfWeek.SUNDAY).plusDays(1)).toList();
    }

    public static LocalDate getActualMonday() {
        LocalDate now = LocalDate.now();
        return now.with(DayOfWeek.MONDAY);
    }

    public static LocalDate getActualSunday() {
        LocalDate now = LocalDate.now();
        return now.with(DayOfWeek.SUNDAY);
    }

    /**
     * @return get the number of days between the two dates, end date include
     */
    public int getLength() {
        return beginning.datesUntil(end).toList().size() + 1;
    }

    /** Checks if date is in the range */
    public void assertDateIsInDateRange(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date can't be null");
        if (beginning.isAfter(date))
            throw new DateRangeException("The date can't be before the beginning date");
        if (end.isBefore(date))
            throw new DateRangeException("The date can't be after the ending date");
    }

    /**
     * Get all days, from a specific date until an end date, the last date is include
     *
     * @param start:
     *            the starting date
     * @param stop:
     *            the end date include
     *
     * @return List of LocalDate in the range of start and stop
     */
    public static List<LocalDate> getDateUntil(LocalDate start, LocalDate stop) {
        if (start == null) {
            throw new IllegalArgumentException("Start date can't be null");
        }
        if (stop == null) {
            throw new IllegalArgumentException("Stop date can't be null");
        }
        List<LocalDate> days = start.datesUntil(stop).collect(Collectors.toList());
        days.add(stop);
        return days;
    }

    public static String getDateString(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Can't get string of null date");
        }
        return date.format(FORMATTER);
    }

    public static LocalDate getDateFromString(String date) {
        if (date == null) {
            throw new IllegalArgumentException("Can't parse null date string");
        }
        return LocalDate.parse(date, FORMATTER);
    }

    public static StringConverter<LocalDate> datePickerEuropeanFormat() {
        return new StringConverter<>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        };
    }
}
