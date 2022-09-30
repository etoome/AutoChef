package ulb.infof307.g07.controllers.agenda;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ulb.infof307.g07.controllers.components.ListEditableController;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.Agenda;
import ulb.infof307.g07.models.agenda.exceptions.AgendaException;
import ulb.infof307.g07.models.agenda.AgendaJson;
import ulb.infof307.g07.models.agenda.DateRange;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The main agenda controller, contains all other agenda controllers, manage everything
 */
public class AgendaController implements Initializable {

    @FXML
    private HBox fx_agendaContainer;
    @FXML
    private VBox fx_itemsContainer;
    @FXML
    private HBox fx_optionsContainer;
    @FXML
    private ListEditableController listeditableController;

    private final AgendaZoneController agendaZone;
    private final AgendaDateController agendaDate;
    private String actualAgendaName;

    public AgendaController() {
        agendaZone = new AgendaZoneController(this::regenerateAgenda);
        agendaDate = new AgendaDateController();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fx_optionsContainer.getChildren().add(agendaDate);
        fx_agendaContainer.getChildren().add(agendaZone);
        fx_itemsContainer.getChildren().add(new AgendaItemsController(agendaZone));

        initListEditable();
        updateWeeklyAgenda();
    }

    private void initListEditable() {
        try {
            listeditableController.start(Agenda.getDAO().getAllAgendaName(), this::setAgenda, (String name) -> {
                actualAgendaName = name;
                return listeditableController.getList();
            }, () -> {
                actualAgendaName = listeditableController.getWrittenText();
                List<String> agendaNames = listeditableController.getList();
                if (createAgenda()) {
                    agendaNames.add(actualAgendaName);
                }
                return agendaNames;
            }, () -> {
                List<String> agendaNames = listeditableController.getList();
                if (deleteAgenda()) {
                    agendaNames.remove(actualAgendaName);
                    actualAgendaName = "";
                }
                return agendaNames;
            });
        } catch (AgendaException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de la récupération de vos agendas", e));
            SceneManager.getInstance().setMainScene("recipe/main.fxml");
        }
    }

    /**
     * Display the "Hebdomadaire" agenda or recreate a new one for the new week
     */
    private void updateWeeklyAgenda() {
        try {
            Agenda agenda = Agenda.getDAO().getAgendaFromName(AgendaDateController.WEEKLY_NAME);
            assert agenda != null;
            if (DateRange.getActualMonday().isEqual(agenda.getDateBegin())) {
                agendaZone.initAgenda(agenda);
            } else {
                agendaDate.setDates(DateRange.getActualMonday(), DateRange.getActualSunday());
                agenda = new Agenda(AgendaDateController.WEEKLY_NAME, agendaDate.getDateStart(),
                        agendaDate.getDateEnd(), AgendaJson.getInstance().getAgendaNumberDay(),
                        AgendaJson.getInstance().getAgendaSettings());
                agendaZone.initAgenda(agenda);
                Agenda.getDAO().updateAgenda(Agenda.getDAO().getIdFromAgenda(AgendaDateController.WEEKLY_NAME), agenda);
            }
        } catch (AgendaException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Problème lors de la récupération de votre agenda hebdomadaire", e));
            SceneManager.getInstance().setMainScene("recipe/main.fxml");
        }
    }

    /**
     * Sets the currently displayed agenda based on the choiceBox.
     *
     * @param name
     *            selected choiceBox index
     */
    private void setAgenda(String name) {
        try {
            if (Agenda.getDAO().isAgendaNameInDb(name)) {
                Agenda agenda = Objects.requireNonNull(Agenda.getDAO().getAgendaFromName(name));
                listeditableController.setListValue(name);
                actualAgendaName = name;
                agendaDate.setDates(agenda.getDateBegin(), agenda.getDateEnd());
                agendaZone.initAgenda(agenda);
            }
        } catch (AgendaException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de l'initialisation de l'agenda", e));
        }
    }

    /**
     * Generate an agenda using agenda settings from AgendaPopup
     */
    private void regenerateAgenda() {
        if (validateIntegrityOfAgenda()) {
            LocalDate start = agendaDate.getDateStart();
            LocalDate end = agendaDate.getDateEnd();
            if (Objects.equals(actualAgendaName, AgendaDateController.WEEKLY_NAME)) {
                // Agenda "hebdomadaire" need to save these settings in a JSON file, and can't
                // change the date
                start = DateRange.getActualMonday();
                end = DateRange.getActualSunday();
                AgendaJson.getInstance().saveAgendaSettings(agendaZone.getNumberPerDaySettings(),
                        agendaZone.getRecipeStyleSettings());
            }
            try {
                agendaZone.initAgenda(new Agenda(actualAgendaName, start, end, agendaZone.getNumberPerDaySettings(),
                        agendaZone.getRecipeStyleSettings()));
            } catch (AgendaException e) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("Problème de la génération de l'agenda", e));
                setAgenda(AgendaDateController.WEEKLY_NAME); // If failed to load the agenda return to the Weekly agenda
            }
        }
    }

    /**
     * Create an agenda, generating a new one or an empty
     */
    private boolean createAgenda() {
        if (validateIntegrityOfAgenda()) {
            try {
                Agenda.getDAO().checkAgendaNameAlreadyExist(actualAgendaName);
                Agenda agenda = new Agenda(actualAgendaName, agendaDate.getDateStart(), agendaDate.getDateEnd());
                agendaZone.initAgenda(agenda);
                return true;
            } catch (AgendaException e) {
                ExceptionManager.getInstance().handleException(
                        new WarningException("Vous ne pouvez pas créer un deuxième agenda " + actualAgendaName, e));
            }
        }
        return false;
    }

    /**
     * Check if values are correct, date start, date end, and the name of the agenda
     *
     * @return boolean, if is correct or not
     */
    private boolean validateIntegrityOfAgenda() {
        if (agendaDate.isStartBeforeEnd()) {
            if (!Objects.equals(actualAgendaName, "")) {
                return true;
            } else {
                new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Problème avec le nom de l'agenda");
            }
        } else {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Problème avec la date de l'agenda");
        }
        return false;
    }

    /**
     * Delete the agenda from everywhere
     */
    private boolean deleteAgenda() {
        if (!Objects.equals(actualAgendaName, AgendaDateController.WEEKLY_NAME)) {
            try {
                Agenda.getDAO().deleteAgendaWithName(actualAgendaName);
            } catch (AgendaException e) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("Problème de la suppression de l'agenda", e));
                return false;
            }
            return true;
        } else {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP,
                    "L'agenda hebdomadaire ne peut pas etre supprimé");
            return false;
        }
    }

    /**
     * JavaFX calls from the save button in the view, save or update the actual agenda in the database, testing if the
     * agenda already exist in the database.
     */
    @FXML
    private void onSaveClick() {
        if (validateIntegrityOfAgenda()) {
            try {
                if (Agenda.getDAO().isAgendaNameInDb(actualAgendaName))
                    updateAgenda();
                else {
                    Agenda.getDAO().createAgenda(
                            agendaZone.getAgenda(actualAgendaName, agendaDate.getDateStart(), agendaDate.getDateEnd()));
                    new PopupInformation(PopupInformation.Styles.SAVE_POPUP,
                            "Agenda \"" + actualAgendaName + "\" bien sauvegardé");
                }
            } catch (AgendaException e) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Erreur lors de la mise à jour des données dans la base donnée", e));
            }
        }
    }

    /**
     * Recreate an agenda getting all information from above and next update the agenda in the database
     */
    private void updateAgenda() throws AgendaException {
        Agenda agenda;
        if (Objects.equals(actualAgendaName, AgendaDateController.WEEKLY_NAME)) {
            // Can't change the date of the agenda HEBDO
            agenda = agendaZone.getAgenda(actualAgendaName, DateRange.getActualMonday(), DateRange.getActualSunday());
            AgendaJson.getInstance().saveAgendaSettings(agendaZone.getNumberPerDaySettings(),
                    agendaZone.getRecipeStyleSettings());
        } else {
            agenda = agendaZone.getAgenda(actualAgendaName, agendaDate.getDateStart(), agendaDate.getDateEnd());
            agendaZone.initAgenda(agenda);
        }
        if (Agenda.getDAO().updateAgenda(Agenda.getDAO().getIdFromAgenda(actualAgendaName), agenda)) {
            new PopupInformation(PopupInformation.Styles.UPDATE_POPUP,
                    "Agenda \"" + actualAgendaName + "\" bien mis à jour");
        } else {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Problème avec la mise à jour de l'agenda");
        }
    }

    /**
     * JavaFX calls from the shopping button in the view, who generate a shopping list from the actual agenda, using the
     * name of the agenda as name for the shopping list
     */
    @FXML
    private void onCreateShoppingListClick() {
        try {
            Agenda.createShoppingList(
                    agendaZone.getAgenda(actualAgendaName, agendaDate.getDateStart(), agendaDate.getDateEnd()));
            new PopupInformation(PopupInformation.Styles.SAVE_POPUP,
                    "La shopping list de l'agenda " + actualAgendaName + ", a bien été créé");
        } catch (AgendaException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Problème lors de la creation de la liste de course avec l'agenda", e));
        }
    }
}
