package ulb.infof307.g07.controllers.agenda.item;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import ulb.infof307.g07.App;
import ulb.infof307.g07.controllers.agenda.popup.PopupController;
import ulb.infof307.g07.controllers.agenda.popup.PopupMenu;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.recipe.Recipe;

/**
 * Mother class for menu controllers in the agenda
 */
public abstract class MenuAgenda extends AnchorPane {

    @FXML
    private Label fx_recipeNumber;
    @FXML
    private Label fx_recipeList;

    // Didn't find a better, you could try a better workaround to drag&drop, but
    // actually I give up
    private static Menu draggedMenu;

    protected Menu menu;
    protected PopupController popup;
    protected PopupMenu popupMenu;

    protected MenuAgenda(Menu menu, String fxmlSource) {
        this.menu = menu;
        SceneManager.getInstance().loadFxmlNode(this, fxmlSource);
        setText();
    }

    protected void setText() {
        // Because of the use in upper object it needs to recheck everytime if an item
        // has been deleted
        StringBuilder displayedString = new StringBuilder();
        for (Recipe recipeType : menu.getAllRecipes()) {
            displayedString.append(recipeType.getName());
            displayedString.append(", ");
        }
        fx_recipeNumber.setText(menu.getNumberRecipe() + " recettes");
        fx_recipeList.setText(displayedString.toString());
    }

    public Menu getMenu() {
        return menu;
    }

    /**
     * Handle the drag of a menu, save the menu in static object draggedMenu
     */
    protected void handleDrag() {
        RecipeAgenda.setDraggedRecipe(null);
        Dragboard db = this.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putImage(new Image(String.valueOf(App.class.getResource("images/menu_item.png"))));
        setDraggedMenu(menu);
        db.setContent(cb);
    }

    protected void displayRecipes() {
        Bounds bounds = this.getBoundsInLocal();
        Bounds screenBounds = this.localToScreen(bounds);
        popup.showPopup(screenBounds.getMaxX() - 15,
                screenBounds.getMinY() + (this.getHeight() / 2) - (popupMenu.getNumberRecipe() / 2) * 100);
    }

    public static Menu getDraggedMenu() {
        return draggedMenu;
    }

    public static void setDraggedMenu(Menu menu) {
        draggedMenu = menu;
    }
}
