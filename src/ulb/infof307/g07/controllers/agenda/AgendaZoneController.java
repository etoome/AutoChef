package ulb.infof307.g07.controllers.agenda;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ulb.infof307.g07.controllers.agenda.popup.PopupAgenda;
import ulb.infof307.g07.controllers.agenda.popup.PopupController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.*;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.recipe.RecipeStyle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The controller who manage all the agenda zone, drag&drop part, menus, recipe display, agenda navigation
 */
public class AgendaZoneController extends AnchorPane {

    private final ArrayList<AgendaBlockController> agendaBlocks;
    @FXML
    private Label fx_weekLabel;
    @FXML
    private HBox fx_agendaBox;

    private final PopupController popupAgenda;
    private final PopupAgenda popupAgendaSettings;

    private static final List<String> dayName = Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi",
            "Samedi", "Dimanche");
    private int actualWeek;
    private final ArrayList<String> weekListString;

    public AgendaZoneController(OnAction.GenerateAgenda generateAgenda) {
        actualWeek = 0;
        agendaBlocks = new ArrayList<>();
        weekListString = new ArrayList<>();

        SceneManager.getInstance().loadFxmlNode(this, "agenda/agenda_zone.fxml");

        List<LocalDate> daysWeek = DateRange.getActualWeek();
        for (int i = 0; i < DateRange.DAYS_PER_WEEK; i++) {
            agendaBlocks.add(new AgendaBlockController(this::updateMenu));
            fx_agendaBox.getChildren().add(agendaBlocks.get(i));
            agendaBlocks.get(i).setDayText(dayName.get(i), daysWeek.get(i));
        }
        setWeeks(daysWeek);
        fx_weekLabel.setText(weekListString.get(0));

        // Set the agenda in the "Hebdomadaire" mod whose little different from others, can't be deleted, ID 1,
        // regenerate
        // automatically every week, just for the actual week
        popupAgendaSettings = new PopupAgenda("Generation d'agenda", generateAgenda);
        popupAgenda = new PopupController(popupAgendaSettings);
    }

    /**
     *
     * @param agenda:
     *            the agenda used to initialize everything
     */
    public void initAgenda(Agenda agenda) {
        if (Objects.equals(agenda.getName(), AgendaDateController.WEEKLY_NAME)) {
            popupAgendaSettings.setSettings(AgendaJson.getInstance().getAgendaNumberDay(),
                    AgendaJson.getInstance().getAgendaSettings());
        }
        agendaBlocks.clear();
        actualWeek = 0;
        List<LocalDate> days = agenda.getDaysWeek();
        for (int i = 0; i < Math.max(DateRange.DAYS_PER_WEEK, days.size()); i++) {
            agendaBlocks.add(new AgendaBlockController(this::updateMenu));
            HBox.setHgrow(agendaBlocks.get(i), Priority.ALWAYS);
            agendaBlocks.get(i).setDayText(dayName.get(i % DateRange.DAYS_PER_WEEK),
                    days.get(actualWeek * DateRange.DAYS_PER_WEEK + i));
            if (i < DateRange.DAYS_PER_WEEK) {
                fx_agendaBox.getChildren().set(i, agendaBlocks.get(i));
            }
            if (agenda.getDays().contains(days.get(i))) {
                if (agenda.getAllMenusForDay(days.get(i)) != null)
                    for (Menu menu : agenda.getAllMenusForDay(days.get(i)))
                        agendaBlocks.get(i).addMenu(menu);
            } else {
                agendaBlocks.get(i).setDisable(true);
            }
        }
        setWeeks(agenda.getDaysWeek());
        changeWeek();
    }

    public int getNumberPerDaySettings() {
        return popupAgendaSettings.getNumberPerDay();
    }

    public Map<RecipeStyle, Integer> getRecipeStyleSettings() {
        return popupAgendaSettings.getRecipeStyle();
    }

    @FXML
    private void onAgendaSettingsClick(MouseEvent e) {
        popupAgenda.showPopup(e.getScreenX(), e.getScreenY());
        e.consume();
    }

    /**
     * Set the label week
     *
     * @param daysWeek
     *            all days of the week
     */
    private void setWeeks(List<LocalDate> daysWeek) {
        weekListString.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.FRENCH);
        for (int i = 0; i < daysWeek.size() / DateRange.DAYS_PER_WEEK; i++) {
            weekListString.add("Semaine du " + daysWeek.get(i * DateRange.DAYS_PER_WEEK).format(formatter) + " au "
                    + daysWeek.get(i * DateRange.DAYS_PER_WEEK + 6).format(formatter));
        }
    }

    /**
     * Swipe between weeks
     */
    private void changeWeek() {
        fx_weekLabel.setText(weekListString.get(actualWeek));
        for (int i = 0; i < DateRange.DAYS_PER_WEEK; i++) {
            fx_agendaBox.getChildren().set(i, agendaBlocks.get(actualWeek * DateRange.DAYS_PER_WEEK + i));
        }
    }

    @FXML
    private void goToPast() {
        if (actualWeek > 0) {
            actualWeek--;
            changeWeek();
        }
    }

    @FXML
    private void goToNext() {
        if ((actualWeek + 1) * DateRange.DAYS_PER_WEEK < agendaBlocks.size()) {
            actualWeek++;
            changeWeek();
        }
    }

    public Agenda getAgenda(String name, LocalDate start, LocalDate end) throws AgendaException {
        Agenda agenda = new Agenda(name, start, end);
        for (AgendaBlockController agendaBlock : agendaBlocks) {
            if (!agendaBlock.isDisable() && agendaBlock.hasMenu())
                agenda.addMenusForDay(agendaBlock.getDay(), agendaBlock.getMenus());
        }
        return agenda;
    }

    /**
     * Function call when a Menu is deleted from menus list, so it deletes all occurrence in the agenda
     *
     * @param menu:
     *            the menu who has been deleted
     */
    public void removeMenu(Menu menu) {
        for (AgendaBlockController agendaBLock : agendaBlocks) {
            int pos = 0;
            for (Menu menus : agendaBLock.getMenus()) {
                if (Objects.equals(menus.getName(), menu.getName())) {
                    agendaBLock.deleteMenu(pos--);
                }
                pos++;
            }
        }
    }

    /**
     * Function call when a Menu is updated, modified in the agenda, all occurrence in the agenda are updated
     *
     * @param menu:
     *            the menu who has been updated
     */
    public void updateMenu(Menu menu) {
        for (AgendaBlockController agendaBLock : agendaBlocks) {
            int pos = 0;
            for (Menu menus : agendaBLock.getMenus()) {
                if (Objects.equals(menus.getName(), menu.getName())) {
                    agendaBLock.updateMenu(pos++, menu);
                }
            }
        }
    }
}
