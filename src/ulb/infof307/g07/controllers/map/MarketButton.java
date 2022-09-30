package ulb.infof307.g07.controllers.map;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.store.exceptions.StoreException;

public class MarketButton extends HBox {

    @FXML
    private Label fx_labelMarket;
    @FXML
    private Label fx_labelPriceShoppingList;

    @FXML
    private HBox fx_container;

    private final MapController mapControllerParent;
    private final String name;

    public MarketButton(String name, MapController mapControllerParent) {
        this.mapControllerParent = mapControllerParent;
        this.name = name;
        SceneManager.getInstance().loadFxmlNode(this, "map/market_name.fxml");
        fx_labelMarket.setText(name);
        setCursor(Cursor.HAND);
        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
                if (mouseEvent.getClickCount() == 2)
                    mapControllerParent.showStoreInfoWindow(name);
        });
    }

    public void highlightBtn() {
        fx_container.getStyleClass().setAll("container-highlight");
    }

    public void removeHighlight() {
        fx_container.getStyleClass().setAll("container");
    }

    public String getText() {
        return fx_labelMarket.getText();
    }

    public void setText(String text) {
        fx_labelMarket.setText(text);
    }

    @FXML
    private void deleteMarket() {
        try {
            mapControllerParent.deleteMarket(name);
        } catch (StoreException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Erreur lors de la suppression d'un magasin", e));
        }
    }

    @FXML
    private void goToMarket() {
        try {
            mapControllerParent.onMarketClicked(fx_labelMarket.getText());
        } catch (StoreException e) {
            ExceptionManager.getInstance()
                    .handleException(new WarningException("Erreur lors du deplacement vers le magasin", e));
        }
    }

    public void setPriceShoppingList(float price) {
        fx_labelPriceShoppingList.setText(price + "€");
    }

    public void removePriceShoppingList() {
        fx_labelPriceShoppingList.setText("");
    }

}
