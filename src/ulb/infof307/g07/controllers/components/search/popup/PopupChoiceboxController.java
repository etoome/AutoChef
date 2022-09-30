package ulb.infof307.g07.controllers.components.search.popup;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g07.managers.scene.SceneManager;

public class PopupChoiceboxController extends HBox {

    @FXML
    private ChoiceBox<String> fx_input;

    @FXML
    private Text fx_title;

    @FXML
    private Text fx_unit;

    private OnChange callback;

    public PopupChoiceboxController() {
        SceneManager.getInstance().loadFxmlNode(this, "components/search_popup_choicebox.fxml");
    }

    public void set(String title, List<String> choices, String selected, String unit, OnChange callback) {
        this.callback = callback;

        fx_title.setText(title);

        fx_input.getItems().addAll(choices);
        fx_input.getSelectionModel().select(selected);

        fx_input.setOnAction(event -> {
            callback.execute(fx_input.getSelectionModel().getSelectedItem());
        });

        fx_unit.setText(unit != null ? " " + unit : null);
    }
}
