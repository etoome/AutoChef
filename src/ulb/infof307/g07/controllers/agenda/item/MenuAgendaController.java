package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ulb.infof307.g07.controllers.agenda.OnAction;
import ulb.infof307.g07.controllers.agenda.popup.PopupController;
import ulb.infof307.g07.controllers.agenda.popup.PopupMenu;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.agenda.exceptions.MenuException;
import ulb.infof307.g07.models.recipe.Recipe;

/**
 * The controller of the menu view in the agenda, display a Menu in an AgendaBlock, need a Menu object
 */
public final class MenuAgendaController extends MenuAgenda {

    @FXML
    private TextField fx_menuText;

    private final OnAction.MenuToRecipe menuToRecipe;
    private final OnAction.UpdateMenu updateMenu;

    public MenuAgendaController(Menu menu, OnAction.MenuToRecipe menuToRecipe, OnAction.UpdateMenu updateMenu) {
        super(menu, "agenda/menu_agenda.fxml");
        this.menuToRecipe = menuToRecipe;
        this.updateMenu = updateMenu;

        fx_menuText.setText(menu.getName());

        popupMenu = new PopupMenu(this::deleteRecipe, this::menuToRecipe);
        popup = new PopupController(popupMenu);

        for (Recipe recipe : menu.getAllRecipes()) {
            popupMenu.addRecipe(recipe);
        }
        setText();
    }

    public void addRecipe(Recipe recipe) {
        popupMenu.addRecipe(recipe);
        try {
            menu.addRecipe(recipe);
        } catch (IllegalArgumentException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Problème lors de l'ajout de la recette dans le menu", e));
        }
        setText();
    }

    public void deleteRecipe(int indexOfRecipe) {
        try {
            menu.deleteRecipe(indexOfRecipe);
        } catch (MenuException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Problème lors de la suppresion d'une recette dans le menu", e));
        }
        setText();
    }

    public void menuToRecipe() {
        menuToRecipe.execute(this, popupMenu.getFirstRecipe());
    }

    @FXML
    private void showMenu() {
        displayRecipes();
    }

    @FXML
    private void deleteMenuClick() {
        this.setDisable(true);
    }

    @FXML
    private void saveMenuClick() {
        try {

            if (Menu.getDAO().checkMenuName(fx_menuText.getText())) {
                // Hey I fixed it :) :D :p
                updateMenu.execute(getMenu());
                if (Menu.getDAO().updateMenu(Menu.getDAO().getIdFromMenu(fx_menuText.getText()), menu)) {
                    new PopupInformation(PopupInformation.Styles.UPDATE_POPUP,
                            "Menu \"" + fx_menuText.getText() + "\" bien modifié");
                }
            } else {
                menu.setName(fx_menuText.getText());
                if (Menu.getDAO().createMenu(menu)) {
                    new PopupInformation(PopupInformation.Styles.SAVE_POPUP,
                            "Menu \"" + fx_menuText.getText() + "\" bien sauvegardé");
                }
            }
        } catch (MenuException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Erreur lors de la sauvegarde du Menu", e));
        }
    }

    @FXML
    private void handleDragDetection() {
        handleDrag();
        deleteMenuClick();
    }
}
