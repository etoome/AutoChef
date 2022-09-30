package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Ingredient;
import ulb.infof307.g07.models.recipe.Instruction;
import ulb.infof307.g07.models.recipe.Recipe;

import java.util.ArrayList;

/**
 * Mother class of EditController & ViewController, used to avoid duplicated code and simplify the handling.
 */
public abstract class RecipeBaseController extends VBox {

    protected final Recipe recipe;

    protected ArrayList<Ingredient> ingredients;
    protected ArrayList<Instruction> instructions;

    @FXML
    private VBox fx_ingredients;
    @FXML
    private VBox fx_instruction;

    protected RecipeBaseController(Recipe recipe, String path, boolean disable) {
        SceneManager.getInstance().loadFxmlNode(this, path);
        this.recipe = recipe;
        fx_ingredients.setDisable(disable);
        fx_instruction.setDisable(disable);
    }

    protected void renderIngredientsInstructions() {
        ingredients = recipe.getIngredients();
        renderIngredients();

        instructions = new ArrayList<>();
        JSONObject object = recipe.getInstruction();
        JSONArray keys = object.names();
        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            String value = object.getString(key);
            instructions.add(new Instruction(Integer.parseInt(key), value));
        }
        renderInstructions();
    }

    /**
     * Clear the fx_ingredients VBOX, & reload all ingredients
     */
    protected void renderIngredients() {
        fx_ingredients.getChildren().clear();
        for (Ingredient ingredient : ingredients) {
            loadIngredient(ingredient);
        }
    }

    /**
     * Remove an ingredient from a recipe
     */
    private void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        renderIngredients();
    }

    /**
     * Clear the fx_instruction VBOX, & reload all instructions
     */
    protected void renderInstructions() {
        fx_instruction.getChildren().clear();
        int i = 0;
        for (Instruction instruction : instructions) {
            instruction.setKey(i++);
            loadInstruction(instruction);
        }
    }

    /**
     * Load an ingredient in a IngredientController and add it to the fx_ingredients VBOX
     *
     * @param ingredient:
     *            the ingredient to load
     */
    private void loadIngredient(Ingredient ingredient) {
        fx_ingredients.getChildren().add(new IngredientController(ingredient, () -> removeIngredient(ingredient)));
    }

    /**
     * Load an instruction in a InstructionController and add it to the fx_instruction VBOX
     *
     * @param instruction:
     *            the instruction to load
     */
    private void loadInstruction(Instruction instruction) {
        fx_instruction.getChildren().add(new InstructionController(instruction, () -> {
            instructions.remove(instruction);
            renderInstructions();
        }));
    }
}
