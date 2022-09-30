package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ulb.infof307.g07.controllers.agenda.OnAction;
import ulb.infof307.g07.controllers.agenda.popup.PopupController;
import ulb.infof307.g07.controllers.agenda.popup.PopupMenu;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.recipe.Recipe;

/**
 * The controller of the menu view in the left list, need a Menu object
 */
public final class MenuItemController extends MenuAgenda {

    @FXML
    private Label fx_titleName;

    private final OnAction.MenuItemDelete menuDeleted;

    public MenuItemController(Menu menu, OnAction.MenuItemDelete menuDeleted) {
        super(menu, "agenda/menu_item.fxml");
        this.menuDeleted = menuDeleted;

        popupMenu = new PopupMenu();
        popup = new PopupController(popupMenu);

        for (Recipe recipe : menu.getAllRecipes()) {
            popupMenu.addRecipeDisabled(recipe);
        }

        fx_titleName.setText(this.menu.getName());
    }

    @FXML
    private void handleDragDetection() {
        handleDrag();
    }

    @FXML
    private void onMenuClick() {
        displayRecipes();
    }

    @FXML
    private void deleteMenuClick() {
        menuDeleted.execute(this);
    }
}
