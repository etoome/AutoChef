package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Display a recipe in edition mod or in display mod, used to update, create, delete, display recipe, switch between two
 * mods. Created in a new stage.
 */
public class RecipeController implements BaseController {

    private Recipe recipe;
    private boolean editMode;
    private boolean viewOnly;
    private OnAction.OnRecipeDelete recipeDelete;
    private OnAction.OnRecipeCreate recipeCreate;

    @FXML
    private VBox fx_container;
    @FXML
    private TextField fx_title;
    @FXML
    private SVGPath fx_delete;
    @FXML
    private SVGPath fx_export;

    public void start(Recipe recipe) {
        this.recipe = recipe;
        editMode = false;
        viewOnly = true;
        viewMod();
    }

    public void start(Recipe recipe, boolean editMode, OnAction.OnRecipeDelete recipeDelete,
            OnAction.OnRecipeCreate recipeCreate) {
        viewOnly = false;
        this.recipe = recipe;
        this.editMode = editMode;
        this.recipeDelete = recipeDelete;
        this.recipeCreate = recipeCreate;
        if (editMode)
            editMod();
        else
            viewMod();
    }

    /**
     * Switch between the view mod and the edit mod, switching between EditController & ViewController
     *
     * @param recipe:
     *            if the recipe were modified
     */
    private void switchViewEdit(Recipe recipe) {
        if (!viewOnly) {
            this.recipe = recipe;
            recipe.setName(fx_title.getText());
            editMode = !editMode;
            fx_container.getChildren().clear();
            if (editMode) {
                editMod();
            } else {
                viewMod();
            }
        } else {
            new PopupInformation(PopupInformation.Styles.UPDATE_POPUP,
                    "Vous pouvez modifié une recette uniquement dans la section recette");
        }
    }

    /**
     * Set in the edit mod, enabling fxml objects
     */
    private void editMod() {
        fx_title.setText(recipe.getName());
        fx_title.setDisable(false);
        fx_delete.setDisable(false);
        fx_delete.setOpacity(1);
        fx_container.getChildren().add(new EditController(recipe, this::createRecipe));
    }

    /**
     * Set in the view mod, disabling everything who could modify the Recipe
     */
    private void viewMod() {
        fx_title.setText(recipe.getName());
        fx_title.setDisable(true);
        fx_delete.setDisable(true);
        fx_delete.setOpacity(0);
        fx_container.getChildren().add(new ViewController(recipe, this::switchViewEdit));
    }

    /**
     * Handle the creation of a recipe in the database, using information from the EditController
     *
     * @param recipe:
     *            The recipe to create in the database
     */
    private void createRecipe(Recipe recipe) {
        String oldRecipeName = recipe.getName();
        Pattern rTitle = Pattern.compile("^([\\w\\d-_]+\\s*)+$", Pattern.CASE_INSENSITIVE);
        if (rTitle.matcher(fx_title.getText()).find()) {
            recipe.setName(fx_title.getText());
        }
        try {
            var recipeIdDB = Recipe.getDAO().getIdFromRecipe(oldRecipeName);
            if (recipeIdDB == null) {
                if (Recipe.getDAO().createRecipe(recipe))
                    recipeCreate.execute(recipe);// Create dynamically in the main page
            } else {
                Recipe.getDAO().updateRecipe(recipe, recipeIdDB);
                recipeCreate.execute(null);// Update dynamically in the main page
            }
            switchViewEdit(recipe);
        } catch (RecipeException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Il y a eu un prolbème lors de la sauvegarde de la recette", e));
        }
    }

    /**
     * Delete the recipe, but test if the recipe even exists, and next close the window of the EditController
     */
    @FXML
    private void deleteRecipe() {
        try {
            if (Recipe.getDAO().checkRecipeName(recipe.getName())) {
                if (recipeDelete != null) {
                    Recipe.getDAO().deleteRecipeWithName(recipe.getName());
                    recipeDelete.execute(recipe);
                }
                Window window = fx_delete.getScene().getWindow();
                if (window instanceof Stage windowStage)
                    windowStage.close();
            } else
                new PopupInformation(PopupInformation.Styles.ERROR_POPUP,
                        "La recette ne peut pas etre supprimé si elle n'existe pas");
        } catch (RecipeException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Il y a eu un prolbème lors de la suppresion de la recette", e));
        }
    }

    /**
     * Export a recipe in a JSON file, opening file chooser and simply saving it in a file .json
     */
    @FXML
    private void exportRecipe() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        var sceneManager = SceneManager.getInstance();
        File file = fileChooser.showSaveDialog(sceneManager.getMainStage());
        if (file != null) {
            try {
                RecipeJSON.getInstance().exportRecipe(recipe, file);
            } catch (IOException e) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Il y a eu un prolbème lors de l'exportation de la recette", e));
            }
        }
    }

}
