package ulb.infof307.g07.controllers.layout;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.utils.InternetConnectionTester;

/**
 * Menu view to switch between every scene in the view
 */
public class HeaderController implements BaseController {
    @FXML
    private HBox fx_agenda;

    @FXML
    private HBox fx_map;

    @FXML
    private HBox fx_recipe;

    @FXML
    private HBox fx_shoppinglist;

    private static final String TITLE_RECIPE = "AutoChef - Recettes";
    private static final String TITLE_AGENDA = "AutoChef - Agenda";
    private static final String TITLE_SHOPPING_LIST = "AutoChef - Listes de courses";
    private static final String TITLE_MAP = "AutoChef - Map";

    @FXML
    private void OnRecipeClick() {
        SceneManager.getInstance().setMainScene("recipe/main.fxml");
        setMainStageTitle(TITLE_RECIPE);
    }

    @FXML
    private void OnAgendaClick() {
        SceneManager.getInstance().setMainScene("agenda/agenda.fxml");
        setMainStageTitle(TITLE_AGENDA);
    }

    @FXML
    private void OnShoppingListClick() {
        SceneManager.getInstance().setMainScene("shoppinglist/main.fxml");
        setMainStageTitle(TITLE_SHOPPING_LIST);
    }

    @FXML
    private void OnMapClick() {
        if (InternetConnectionTester.isConnected()) {
            SceneManager.getInstance().setMainScene("map/map_main.fxml");
            setMainStageTitle(TITLE_MAP);
        } else {
            ExceptionManager.getInstance()
                    .handleException(new WarningException("Vous devez être connecté à l'internet pour utiliser la map",
                            new Exception("Launched map with no internet connection")));
        }
    }

    private void setMainStageTitle(String name) {
        var sceneManager = SceneManager.getInstance();
        sceneManager.getMainStage().setTitle(name);
    }
}
