package ulb.infof307.g07.controllers.agenda;

import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.DateRange;

import java.time.LocalDate;
import java.util.List;

public class AgendaDateController extends HBox {

    @FXML
    private DatePicker fx_dateStart;
    @FXML
    private DatePicker fx_dateEnd;

    public static final String WEEKLY_NAME = "Hebdomadaire";

    public AgendaDateController() {
        SceneManager.getInstance().loadFxmlNode(this, "agenda/agenda_date.fxml");
        initDate();
    }

    /**
     * Initialize datePickers based on a tutorial from
     * <a href="https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/date-picker.htm">link</a>
     */
    private void initDate() {
        List<LocalDate> daysWeek = DateRange.getActualWeek();
        fx_dateStart.setConverter(DateRange.datePickerEuropeanFormat());
        fx_dateEnd.setConverter(DateRange.datePickerEuropeanFormat());
        fx_dateStart.setValue(daysWeek.get(0));
        final Callback<DatePicker, DateCell> dayCellFactory = callBack -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(fx_dateStart.getValue())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #9E9E9E;");
                }
            }
        };
        fx_dateEnd.setDayCellFactory(dayCellFactory);
        fx_dateEnd.setValue(daysWeek.get(daysWeek.size() - 1));
    }

    public boolean isDateStartEqual(LocalDate date) {
        return fx_dateStart.getValue().equals(date);
    }

    public boolean isStartBeforeEnd() {
        return !fx_dateStart.getValue().isAfter(fx_dateEnd.getValue());
    }

    public LocalDate getDateStart() {
        return fx_dateStart.getValue();
    }

    public LocalDate getDateEnd() {
        return fx_dateEnd.getValue();
    }

    public void setDates(LocalDate start, LocalDate end) {
        fx_dateStart.setValue(start);
        fx_dateEnd.setValue(end);
    }

}
