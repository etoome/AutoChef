package ulb.infof307.g07.controllers.recipe;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.Instruction;

/**
 * Instructions display controller in a recipe, could switch between view and edition mod use RecipeInstruction model.
 */
public class InstructionController extends HBox implements BaseController {

    @FXML
    private SVGPath fx_delete;
    @FXML
    private TextArea fx_instruction;
    @FXML
    private Text fx_key;

    private final Instruction instruction;
    private final OnAction.OnInstructionDelete onDelete;

    public InstructionController(Instruction instruction, OnAction.OnInstructionDelete onDelete) {
        this.instruction = instruction;
        this.onDelete = onDelete;

        SceneManager.getInstance().loadFxmlNode(this, "recipe/instruction.fxml");

        fx_instruction.setOnKeyTyped(event -> {
            instruction.setValue(fx_instruction.getText());
            event.consume();
        });

        render();
    }

    private void render() {
        fx_key.setText(String.valueOf(instruction.getKey() + 1));
        fx_instruction.setText(instruction.getValue());
    }

    @FXML
    private void deleteInstruction() {
        onDelete.execute();
    }

}
