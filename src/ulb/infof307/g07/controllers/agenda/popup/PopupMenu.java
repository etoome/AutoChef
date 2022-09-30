package ulb.infof307.g07.controllers.agenda.popup;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import ulb.infof307.g07.controllers.agenda.OnAction;
import ulb.infof307.g07.controllers.agenda.item.RecipeAgendaController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Recipe;
import ulb.infof307.g07.models.recipe.RecipeType;

import java.util.ArrayList;
import java.util.List;

/**
 * The PopupMenu controller in the agenda block, used to display all recipes in from a menu in the agenda
 */
public class PopupMenu extends VBox {

    private OnAction.RecipeInMenuAgendaDelete recipeDelete;
    private OnAction.PopupMenuToRecipe menuToRecipe;

    public PopupMenu() {
        SceneManager.getInstance().loadFxmlNode(this, "agenda/menu_popup.fxml");
    }

    public PopupMenu(OnAction.RecipeInMenuAgendaDelete recipeDelete, OnAction.PopupMenuToRecipe menuToRecipe) {
        this.recipeDelete = recipeDelete;
        this.menuToRecipe = menuToRecipe;
        SceneManager.getInstance().loadFxmlNode(this, "agenda/menu_popup.fxml");
    }

    /**
     * Add a recipe to the VBox object, initialize all JavaFX listener before adding it Is a sort of work around because
     * RecipeAgendaController is used in 3 different places
     *
     * @param recipe:
     *            the recipe to display
     */
    public void addRecipe(Recipe recipe) {
        RecipeAgendaController rac = new RecipeAgendaController(recipe);
        this.getChildren().add(rac);
        this.setPrefHeight(getNumberRecipe() * RecipeAgendaController.RECIPE_ITEM_HEIGHT);
        rac.disableProperty().addListener((obs, oldValue, newValue) -> removeRecipe(rac));
    }

    public void addRecipeDisabled(Recipe recipe) {
        RecipeAgendaController rac = new RecipeAgendaController(recipe);
        rac.showOnly();
        this.getChildren().add(rac);
        this.setPrefHeight(getNumberRecipe() * RecipeAgendaController.RECIPE_ITEM_HEIGHT);
    }

    public double getNumberRecipe() {
        return this.getChildren().size();
    }

    private void removeRecipe(RecipeAgendaController rac) {
        int indexOfRecipe = this.getChildren().indexOf(rac);
        this.getChildren().remove(rac);
        this.setPrefHeight(this.getHeight() - RecipeAgendaController.RECIPE_ITEM_HEIGHT);
        if (this.getChildren().size() == 1) {
            menuToRecipe.execute();
            this.setVisible(false);
        } else
            recipeDelete.execute(indexOfRecipe);
    }

    public List<RecipeType> getRecipeStyles() {
        ArrayList<RecipeType> recipeTypes = new ArrayList<>();
        for (Node recipeNode : this.getChildren()) {
            RecipeAgendaController recipe = (RecipeAgendaController) recipeNode;
            recipeTypes.add(recipe.getRecipe().getType());
        }
        return recipeTypes;
    }

    public Recipe getFirstRecipe() {
        RecipeAgendaController recipe = (RecipeAgendaController) this.getChildren().get(0);
        return recipe.getRecipe();
    }
}
