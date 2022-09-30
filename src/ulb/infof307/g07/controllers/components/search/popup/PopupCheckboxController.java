package ulb.infof307.g07.controllers.components.search.popup;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g07.managers.scene.SceneManager;

public class PopupCheckboxController extends HBox {

    @FXML
    private CheckBox fx_input;

    @FXML
    private Text fx_title;

    private OnChange callback;

    public PopupCheckboxController() {
        SceneManager.getInstance().loadFxmlNode(this, "components/search_popup_checkbox.fxml");
    }

    public void set(String title, String value, OnChange callback) {
        this.callback = callback;

        fx_title.setText(title);
        fx_input.setSelected(value == State.ON.getState());
    }

    @FXML
    void OnChange() {
        callback.execute(fx_input.isSelected() ? State.ON.getState() : State.OFF.getState());
    }
}
