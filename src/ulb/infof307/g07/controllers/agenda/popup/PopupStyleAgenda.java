package ulb.infof307.g07.controllers.agenda.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.RecipeStyle;

import java.util.Objects;

/**
 * popup_style_option controller, super simple object who only display all available recipe style with a textField
 */
public class PopupStyleAgenda extends AnchorPane {

    @FXML
    private Label fx_styleName;
    @FXML
    private TextField fx_styleNumber;

    private final RecipeStyle style;

    public PopupStyleAgenda(RecipeStyle style) {
        this.style = style;
        SceneManager.getInstance().loadFxmlNode(this, "agenda/popup_style_option.fxml");
        fx_styleName.setText(style.toString());
    }

    public RecipeStyle getRecipeStyle() {
        return style;
    }

    public int getNumber() {
        int nb = 0;
        if (!Objects.equals(fx_styleNumber.getText(), "")) {
            try {
                nb = Integer.parseInt(fx_styleNumber.getText());
                if (nb < 0) {
                    nb = 0;
                }
            } catch (NumberFormatException e) {
                ExceptionManager.getInstance()
                        .handleException(new WarningException("Problème avec votre nombre de recette par jour", e));
            }
        }
        return nb;
    }

    public void setStyle(int styleNumber) {
        if (styleNumber > 0)
            fx_styleNumber.setText(String.valueOf(styleNumber));
    }
}
