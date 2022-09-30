package ulb.infof307.g07.controllers.recipe;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.json.JSONObject;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.models.recipe.*;
import ulb.infof307.g07.models.recipe.exceptions.RecipeException;
import ulb.infof307.g07.utils.Colors;
import ulb.infof307.g07.utils.Delay;

/**
 * Display a recipe in edition mod used to update, create, delete, used by the RecipeController.
 */
public final class EditController extends RecipeBaseController {

    @FXML
    private SVGPath fx_edit;
    @FXML
    private SVGPath fx_rule;
    @FXML
    private TextField fx_people;
    @FXML
    private TextField fx_time;
    @FXML
    private VBox fx_ingredients;
    @FXML
    private VBox fx_instruction;
    @FXML
    private ChoiceBox<String> fx_type;
    @FXML
    private ChoiceBox<String> fx_style;

    private final OnAction.OnRecipeCreate recipeCreate;

    public EditController(Recipe recipe, OnAction.OnRecipeCreate recipeCreate) {
        super(recipe, "recipe/edit.fxml", false);
        this.recipeCreate = recipeCreate;
        render();
    }

    /**
     * Render all JavaFX components setting value from the recipe object
     */
    private void render() {
        fx_people.setText(String.valueOf(recipe.getNumberPeople()));
        renderRule();
        fx_time.setText(String.valueOf(recipe.getTime()));
        fx_type.setItems(FXCollections.observableArrayList(RecipeType.DRINK.toString(), RecipeType.SOUP.toString(),
                RecipeType.ENTREE.toString(), RecipeType.MEAL.toString(), RecipeType.DESSERT.toString(),
                RecipeType.STEWED.toString()));
        fx_type.getSelectionModel().select(recipe.getType().toString());
        fx_style.setItems(FXCollections.observableArrayList(RecipeStyle.FISH.toString(), RecipeStyle.MEAT.toString(),
                RecipeStyle.VEGAN.toString(), RecipeStyle.VEGGIE.toString()));
        fx_style.getSelectionModel().select(recipe.getStyle().toString());
        renderIngredientsInstructions();
    }

    private void renderRule() {
        try {
            fx_rule.setDisable(Integer.parseInt(fx_people.getText()) == recipe.getNumberPeople());
        } catch (Exception exception) {
            fx_rule.setDisable(true);
        }
    }

    @FXML
    private void refreshQuantity() throws RecipeException {
        fx_rule.setFill(Colors.DISABLE.getColor());
        Delay.wait(500, () -> {
            if (fx_rule.getFill() != Colors.ERROR.getColor()) {
                fx_rule.setFill(Colors.ENABLE.getColor());
            }
        });

        for (Ingredient ingredient : ingredients) {
            float oldQuantity = ingredient.getQuantityValue();
            float newQuantity = (oldQuantity / recipe.getNumberPeople()) * getNumberPeople();
            ingredient.setQuantity(newQuantity);
        }
        recipe.setNumberPeople(getNumberPeople());
        renderRule();
        renderIngredients();
    }

    @FXML
    private void OnEditNbrPeople() {
        renderRule();
    }

    @FXML
    private void addIngredients() {
        ingredients.add(new Ingredient(""));
        renderIngredients();
    }

    @FXML
    private void addInstruction() {
        instructions.add(new Instruction(instructions.size(), ""));
        renderInstructions();
    }

    private int getNumberPeople() {
        int nbPeople = 0;
        try {
            nbPeople = Integer.parseInt(fx_people.getText());
            assert nbPeople < 0;
        } catch (NullPointerException e) {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Mauvais nombre de personnes");
        }
        return nbPeople;
    }

    private int getTime() {
        int time = 0;

        try {
            time = Integer.parseInt(fx_time.getText());
            assert time < 0;
        } catch (NullPointerException e) {
            new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Mauvais temps pour la recette");
        }

        return time;
    }

    /**
     * Initialize a recipe from all fields and set this.recipe, perform all regex checks.
     *
     * @throws RecipeException:
     *             the possible RecipeException exception handled further
     */
    private void initRecipeFromFields() throws RecipeException {
        recipe.setNumberPeople(getNumberPeople());
        recipe.setTime(getTime());

        String type = fx_type.getSelectionModel().getSelectedItem();
        RecipeType recipeType = RecipeType.getValue(type);
        if (recipeType != null) {
            recipe.setType(recipeType);
        }

        String style = fx_style.getSelectionModel().getSelectedItem();
        RecipeStyle recipeStyle = RecipeStyle.getValue(style);
        if (recipeStyle != null) {
            recipe.setStyle(recipeStyle);
        }

        recipe.setIngredients(ingredients);

        JSONObject jsonInstruction = new JSONObject();
        for (Instruction instruction : instructions) {
            jsonInstruction.put(String.valueOf(instruction.getKey()), instruction.getValue());
        }
        recipe.setInstruction(jsonInstruction);
    }

    /**
     * Save or update if the recipe already exist the recipe in the database
     */
    @FXML
    private void onSaveClick() {
        try {
            initRecipeFromFields();
            recipeCreate.execute(recipe);
        } catch (RecipeException e) {
            ExceptionManager.getInstance().handleException(new ErrorException(
                    "Il y a eu un prolbème lors de la récupération des informations de la recette", e));
        }
    }
}
