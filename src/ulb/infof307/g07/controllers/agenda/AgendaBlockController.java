package ulb.infof307.g07.controllers.agenda;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import ulb.infof307.g07.controllers.agenda.item.MenuAgenda;
import ulb.infof307.g07.controllers.agenda.item.MenuAgendaController;
import ulb.infof307.g07.controllers.agenda.item.RecipeAgenda;
import ulb.infof307.g07.controllers.agenda.item.RecipeAgendaController;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.agenda.DateRange;
import ulb.infof307.g07.models.agenda.Menu;
import ulb.infof307.g07.models.recipe.Recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller of an agenda block, a day from the agenda, where recipes and menus can be placed
 */
public class AgendaBlockController extends VBox {

    @FXML
    private Label fx_dayName;
    @FXML
    private Label fx_dayDate;
    @FXML
    private VBox fx_agendaBlocks;
    @FXML
    private ScrollPane fx_scrollPane;

    private LocalDate day;
    private final OnAction.UpdateMenu updateMenu;

    public AgendaBlockController(OnAction.UpdateMenu updateMenu) {
        SceneManager.getInstance().loadFxmlNode(this, "agenda/agenda_block.fxml");
        this.updateMenu = updateMenu;
    }

    /**
     * Create a MenuAgendaController object to display a menu with all javaFX listener initialize, simplifies the
     * deletion of a menu, or the transformation from a menu to a recipe
     *
     * @param menu:
     *            The Menu to display in the block
     *
     * @return a MenuAgendaController object
     */
    private MenuAgendaController getMenuAgendaListener(Menu menu) {
        MenuAgendaController mac = new MenuAgendaController(menu, this::menuToRecipe, updateMenu);
        mac.disableProperty()
                .addListener((observableValue, oldValue, newValue) -> fx_agendaBlocks.getChildren().remove(mac));
        return mac;
    }

    /**
     * Create a RecipeAgendaController object to display a recipe with all javaFX listener initialize Is a sort of work
     * around because RecipeAgendaController is used in 3 different places
     *
     * @param recipe:
     *            The recipe to display in the block
     *
     * @return a RecipeAgendaController object
     */
    private RecipeAgendaController getRecipeAgendaListener(Recipe recipe) {
        RecipeAgendaController rmc = new RecipeAgendaController(recipe);
        rmc.disableProperty()
                .addListener((observableValue, oldValue, newValue) -> fx_agendaBlocks.getChildren().remove(rmc));
        return rmc;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDayText(String name, LocalDate date) {
        day = date;
        fx_dayName.setText(name);
        fx_dayDate.setText(DateRange.getDateString(date));
    }

    /**
     * Add a RecipeAgendaController or MenuAgendaController object, depending on if the menu has only one recipe or not
     *
     * @param menu:
     *            the menu object used to create objects to display
     */
    public void addMenu(Menu menu) {
        if (menu.getAllRecipes().size() == 1)
            fx_agendaBlocks.getChildren().add(getRecipeAgendaListener(menu.getRecipe(0)));
        else
            fx_agendaBlocks.getChildren().add(getMenuAgendaListener(menu));
    }

    public void menuToRecipe(MenuAgendaController mac, Recipe recipe) {
        fx_agendaBlocks.getChildren().set(fx_agendaBlocks.getChildren().indexOf(mac), getRecipeAgendaListener(recipe));
        fx_agendaBlocks.getChildren().remove(mac);
    }

    public List<Menu> getMenus() {
        ArrayList<Menu> menus = new ArrayList<>();
        for (int i = 0; i < fx_agendaBlocks.getChildren().size(); i++) {
            if (fx_agendaBlocks.getChildren().get(i).getClass() == RecipeAgendaController.class) {
                RecipeAgendaController recipeAgenda = (RecipeAgendaController) fx_agendaBlocks.getChildren().get(i);
                menus.add(new Menu(recipeAgenda.getRecipe().getName(), recipeAgenda.getRecipe()));
            } else {
                MenuAgendaController menuAgenda = (MenuAgendaController) fx_agendaBlocks.getChildren().get(i);
                menus.add(menuAgenda.getMenu());
            }
        }
        return menus;
    }

    @FXML
    private void handleDragOverRecipe(DragEvent event) {
        if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
        event.consume();
    }

    /**
     * A javaFX function who handle the drop of an object in the AgendaBlock, it handle it using position of the mouse,
     * and the position of objects, to konw what to do. Could drop Menus and Recipes, and recipe can merge to create a
     * menu
     *
     * @param event:
     *            javaFX object used to get mouse position
     */
    @FXML
    private void handleDropRecipe(DragEvent event) {
        double posY = event.getY() - 51;
        // Add the scroll position
        posY += fx_scrollPane.getVvalue() * (3 + fx_agendaBlocks.getHeight() - fx_scrollPane.getHeight());
        int last = fx_agendaBlocks.getChildren().size() - 1;
        if (RecipeAgenda.getDraggedRecipe() == null) {
            dropMenu();
        } else {
            if (fx_agendaBlocks.getChildren().isEmpty() || event.getY() - 31 > (fx_scrollPane.getHeight())
                    || fx_agendaBlocks.getChildren().get(last).getLayoutY()
                            + RecipeAgendaController.RECIPE_ITEM_HEIGHT < posY) { // If the items is at the end of the
                                                                                  // block
                fx_agendaBlocks.getChildren().add(getRecipeAgendaListener(RecipeAgenda.getDraggedRecipe()));
            } else { // If a recipe is drop on an other recipe or menu, create a menu or add to menu
                for (int i = 0; i < fx_agendaBlocks.getChildren().size(); i++) {
                    if (fx_agendaBlocks.getChildren().get(i).getLayoutY() <= posY
                            && posY < fx_agendaBlocks.getChildren().get(i).getLayoutY()
                                    + RecipeAgendaController.RECIPE_ITEM_HEIGHT) {
                        dropRecipeMenu(i);
                    }
                }
            }
            RecipeAgenda.setDraggedRecipe(null);
        }
        event.consume();
    }

    /**
     * Drop a recipe to a specific children of the agenda_blocks, handle the creation and the addition in a menu
     *
     * @param who:
     *            the children to modify
     */
    private void dropRecipeMenu(int who) {
        if (fx_agendaBlocks.getChildren().get(who).getClass() == RecipeAgendaController.class) {
            createAMenu(who);
        } else
            addToMenu(who);
    }

    /**
     * Method call when a recipe is drop on another recipe, and it creates a menu element
     *
     * @param who:
     *            the position in the agenda block controller who's the recipe to transform in menu
     */
    private void createAMenu(int who) {
        Menu menu = new Menu("Menu");
        RecipeAgendaController recipeAgenda = (RecipeAgendaController) fx_agendaBlocks.getChildren().get(who);
        try {
            menu.addRecipe(recipeAgenda.getRecipe());
            menu.addRecipe(RecipeAgenda.getDraggedRecipe());
        } catch (IllegalArgumentException e) {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Probleme avec l'ajout de la recette au menu");
        }
        fx_agendaBlocks.getChildren().set(who, getMenuAgendaListener(menu));
    }

    private void addToMenu(int who) {
        MenuAgendaController menuAgenda = (MenuAgendaController) fx_agendaBlocks.getChildren().get(who);
        menuAgenda.addRecipe(RecipeAgenda.getDraggedRecipe());
        fx_agendaBlocks.getChildren().set(who, menuAgenda);
    }

    private void dropMenu() {
        fx_agendaBlocks.getChildren().add(getMenuAgendaListener(MenuAgenda.getDraggedMenu()));
        MenuAgenda.setDraggedMenu(null);
    }

    public void deleteMenu(int pos) {
        fx_agendaBlocks.getChildren().remove(pos);
    }

    public boolean hasMenu() {
        return !fx_agendaBlocks.getChildren().isEmpty();
    }

    public void updateMenu(int i, Menu menu) {
        fx_agendaBlocks.getChildren().set(i, getMenuAgendaListener(menu));
    }
}
