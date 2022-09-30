package ulb.infof307.g07.controllers.agenda.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ulb.infof307.g07.controllers.agenda.OnAction;
import ulb.infof307.g07.controllers.components.PopupInformation;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.recipe.RecipeStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The mother class who display settings in for the agenda generation, used to get all basics information for the
 * generation of the Agenda
 */
public class PopupAgenda extends VBox {

    @FXML
    private VBox fx_styleContainer;
    @FXML
    private TextField fx_numberDay;
    @FXML
    private Label fx_popupTitle;

    private final OnAction.GenerateAgenda generateAgenda;
    private final ArrayList<PopupStyleAgenda> stylesAgenda;

    public PopupAgenda(String title, OnAction.GenerateAgenda generateAgenda) {
        stylesAgenda = new ArrayList<>();
        this.generateAgenda = generateAgenda;
        SceneManager.getInstance().loadFxmlNode(this, "agenda/agenda_popup.fxml");
        fx_popupTitle.setText(title);
        for (RecipeStyle style : RecipeStyle.values()) {
            stylesAgenda.add(new PopupStyleAgenda(style));
            fx_styleContainer.getChildren().add(stylesAgenda.get(stylesAgenda.size() - 1));
        }
    }

    /**
     * Get all RecipeStyle from the popup
     *
     * @return settings to create an agenda
     */
    public Map<RecipeStyle, Integer> getRecipeStyle() {
        HashMap<RecipeStyle, Integer> settingGeneration = new HashMap<>();

        for (int i = 0; i < fx_styleContainer.getChildren().size(); i++) {
            PopupStyleAgenda tempStyle = (PopupStyleAgenda) fx_styleContainer.getChildren().get(i);
            settingGeneration.put(tempStyle.getRecipeStyle(), tempStyle.getNumber());
        }
        return settingGeneration;
    }

    public int getNumberPerDay() {
        int nb = 0;
        if (!Objects.equals(fx_numberDay.getText(), "")) {
            try {
                nb = Integer.parseInt(fx_numberDay.getText());
                if (nb <= 0) {
                    nb = 0;
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                new PopupInformation(PopupInformation.Styles.ERROR_POPUP, "Mauvais nombre de recette par jour");
            }
        }
        return nb;
    }

    public void setSettings(int numberPerDay, Map<RecipeStyle, Integer> recipeStyles) {
        if (numberPerDay > 0)
            this.fx_numberDay.setText(String.valueOf(numberPerDay));
        for (PopupStyleAgenda popupStyleAgenda : stylesAgenda) {
            if (recipeStyles.containsKey(popupStyleAgenda.getRecipeStyle()))
                popupStyleAgenda.setStyle(recipeStyles.get(popupStyleAgenda.getRecipeStyle()));
        }
    }

    @FXML
    private void regenerateAgenda() {
        generateAgenda.execute();
    }
}
