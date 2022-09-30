package ulb.infof307.g07.controllers.components.search.popup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ulb.infof307.g07.managers.scene.SceneManager;

public class PopupIntController extends HBox {

    @FXML
    private TextField fx_input;

    @FXML
    private Text fx_title;

    @FXML
    private Text fx_unit;

    private OnChange callback;

    private String oldInput = "";

    public PopupIntController() {
        SceneManager.getInstance().loadFxmlNode(this, "components/search_popup_int.fxml");
    }

    public void set(String title, String value, String unit, OnChange callback) {
        this.callback = callback;

        fx_title.setText(title);
        fx_input.setText(value);

        fx_unit.setText(unit != null ? " " + unit : null);
    }

    @FXML
    void OnInput() {
        String text = fx_input.getText();
        Pattern regexPattern = Pattern.compile("^\\d*$", Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = regexPattern.matcher(text);
        if (regexMatcher.find()) {
            oldInput = text;
            callback.execute(text);
        } else {
            fx_input.setText(oldInput);
        }
    }
}
