package ulb.infof307.g07.controllers.map;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import ulb.infof307.g07.controllers.BaseController;
import netscape.javascript.JSObject;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.exception.types.WarningException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;
import ulb.infof307.g07.models.store.Home;
import ulb.infof307.g07.models.store.exceptions.HomeException;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapController implements BaseController, Initializable {
    @FXML
    private VBox fx_shoppingListList;
    @FXML
    private ScrollPane fx_productScrollMap;
    @FXML
    private VBox fx_productListMap;
    @FXML
    private WebView fx_mapWebview;
    @FXML
    private ComboBox<String> fx_ComboBoxShoppingList;
    @FXML
    private Label fx_errorLabelSl;
    @FXML
    private Label fx_shoppingListLabel;
    @FXML
    private VBox fx_shoppingListContent;

    private WebEngine fx_mapWebengine;
    private final MapJS bridge; // Doit etre en attribut pour ne pas etre delete pas le GC

    static final String errorNotMarketForShoppingList = "Aucun magasin ne contient tous les produits de cette liste de course";
    static final String shoppingListBoxTitle = "Contenu de la liste de course";

    private final ObservableList<String> shoppingListsNames;
    private final HashMap<String, ShoppingList> shoppingLists = new HashMap<>();

    public MapController() {
        bridge = new MapJS(this);
        shoppingListsNames = FXCollections.observableArrayList();
        try {
            for (var shoppingList : Objects.requireNonNull(ShoppingList.getDAO().getAllShoppingLists())) {
                if (!shoppingList.isArchived()) {
                    var shoppingListName = shoppingList.getName();
                    shoppingListsNames.add(shoppingListName);
                    shoppingLists.put(shoppingListName, shoppingList);
                }
            }
        } catch (ShoppingListException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Une erreur est survenue lors de la récupération des listes de courses", e));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fx_ComboBoxShoppingList.setItems(shoppingListsNames);
        fx_ComboBoxShoppingList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> setError("")); // Clear error message
        fx_ComboBoxShoppingList.setOnAction((event) -> {
            try {
                fillContentShoppingList();
            } catch (ShoppingListException e) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Une erreur est survenue lors du remplissage de la shopping list", e));
            }
        });
        fx_productScrollMap.setContent(fx_productListMap);
        fx_mapWebview.setContextMenuEnabled(false);
        fx_mapWebengine = fx_mapWebview.getEngine();
        URL urlHtmlPage = this.getClass().getResource("/ulb/infof307/g07/map/map.html");
        assert urlHtmlPage != null;
        fx_mapWebengine.load(urlHtmlPage.toExternalForm());
        fx_mapWebengine.setJavaScriptEnabled(true);
        try {
            fx_mapWebengine.getLoadWorker().stateProperty()
                    .addListener((ChangeListener<? super Worker.State>) (observable, oldValue, newValue) -> {
                        if (newValue != Worker.State.SUCCEEDED) {
                            return;
                        }
                        JSObject window = (JSObject) fx_mapWebengine.executeScript("window");
                        window.setMember("app", bridge);
                        try {
                            for (Store store : Store.getDAO().getAllStores()) {
                                addStoreList(store.getName());
                                try {
                                    fx_mapWebengine.executeScript("createStoreMarker(\"" + store.getName() + "\", "
                                            + store.getLat() + ", " + store.getLng() + ")");
                                } catch (Exception e) {
                                    ExceptionManager.getInstance().handleException(
                                            new ErrorException("Le moteur web n'a pas pu executer un script", e));
                                    SceneManager.getInstance().setMainScene("map/map_main.fxml");
                                }
                            }
                        } catch (StoreException e) {
                            ExceptionManager.getInstance()
                                    .handleException(new ErrorException("Erreur durant le chargement des magasins", e));
                        }
                        Home home = null;
                        try {
                            home = Home.getDAO().getCurrentHome();
                        } catch (HomeException exception) {
                            ExceptionManager.getInstance().handleException(new ErrorException(
                                    "Erreur lors de la récupération des coordonnées de la maison", exception));
                        }
                        if (home != null)
                            fx_mapWebengine.executeScript("loadHome(" + home.getLat() + ", " + home.getLng() + ")");
                    });
            fx_ComboBoxShoppingList.valueProperty().addListener(e -> {
                fx_mapWebengine.executeScript("refreshMapInfos()");
                hideAllStoresWithout();
                cleanPriceByStore();
            });
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(new ErrorException(
                    "Erreur lors de l'intercation avec la carte (communication Java - JavaScript)", e));
        }

    }

    /**
     * Method that getting the Storss with all the products, sends a string ( for JS ) of a list containing the
     * coordonates of those stores, the form of the string is 'x1,y1; x2,y2;...'
     */
    @FXML
    private String getCoordinatesOfStores(ArrayList<Store> storesThatGotEverything) {
        StringBuilder coordonatesOfStores = new StringBuilder();
        for (var store : storesThatGotEverything) {
            var lat = store.getLat();
            var lng = store.getLng();
            coordonatesOfStores.append(lat).append(",").append(lng).append(";");
        }
        StringBuilder sb = new StringBuilder(coordonatesOfStores.toString());
        if (sb.length() >= 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Computes to get the closest store
     */
    @FXML
    private void computeClosestStore() {
        var storeName = fx_ComboBoxShoppingList.getValue();
        if (storeName != null) {
            Home home = null;
            try {
                home = Home.getDAO().getCurrentHome();
            } catch (HomeException exception) {
                ExceptionManager.getInstance().handleException(new WarningException("Aucun repère de Home", exception));
            }
            if (home != null) {
                try {
                    ArrayList<Store> storesThatGotEverything = Store.getDAO()
                            .getStoresWithAllProductsFrom(shoppingLists.get(storeName)); // gets the Stores with
                                                                                         // evrythong
                    if (!storesThatGotEverything.isEmpty()) {
                        String coordonatesOfAllStores = getCoordinatesOfStores(storesThatGotEverything);
                        fx_mapWebengine.executeScript("computeShortestPath(\"" + coordonatesOfAllStores + "\")");
                    } else {
                        setError(errorNotMarketForShoppingList);
                    }
                } catch (StoreException e) {
                    ExceptionManager.getInstance()
                            .handleException(new ErrorException("Erreur lors de la récupération des magasins", e));
                }
            }
        }
    }

    /**
     * method that reduce the opacity of the stores that do nothave all the product of the selected shopping list
     */
    private void hideAllStoresWithout() {
        var shoppingListValue = fx_ComboBoxShoppingList.getValue();
        if (shoppingListValue != null) {
            ArrayList<Store> allStoresWithout = null;
            try {
                allStoresWithout = Store.getStoresWithoutFrom(shoppingLists.get(shoppingListValue));
            } catch (StoreException e) {
                ExceptionManager.getInstance().handleException(new ErrorException(
                        "Erreur lors de la récupération des magasins ne contenant pas les produits de la liste de course",
                        e));
            }
            fx_mapWebengine.executeScript("refreshAllStore()");
            if (allStoresWithout != null) {
                for (Store store : allStoresWithout) {
                    fx_mapWebengine.executeScript("hideStore(" + store.getLat() + ", " + store.getLng() + ")");
                }
            } else
                setError(errorNotMarketForShoppingList);
        }
    }

    /**
     * Compute the total price of all the stores that contains all the product for the selected shoppingList
     */
    @FXML
    private void computePriceByStore() {
        var currentShoppingList = shoppingLists.get(fx_ComboBoxShoppingList.getValue());
        if (currentShoppingList != null) {
            if (!currentShoppingList.isEmpty()) {
                try {
                    for (var elem : Store.getDAO().getListOfPriceByStore(currentShoppingList).entrySet()) {
                        for (var i = 0; i < fx_productListMap.getChildren().size(); i++) {
                            if (fx_productListMap.getChildren().get(i) instanceof MarketButton btn) {
                                if (Objects.equals(btn.getText(), elem.getKey().getName())) {
                                    btn.setPriceShoppingList(elem.getValue());
                                }
                            }
                        }
                    }
                } catch (StoreException e) {
                    ExceptionManager.getInstance().handleException(new ErrorException("Une erreur est survenue", e));
                }
            }
        }
    }

    /**
     * method that erases all the displayed price
     */
    private void cleanPriceByStore() {
        for (var i = 0; i < fx_productListMap.getChildren().size(); i++) {
            if (fx_productListMap.getChildren().get(i) instanceof MarketButton btn) {
                btn.removePriceShoppingList();
            }
        }
    }

    /**
     * Computes to get the cheapest store
     */
    @FXML
    private void computeCheapestStore() {
        computePriceByStore();
        var shoppingListName = fx_ComboBoxShoppingList.getValue();
        if (shoppingListName != null) {
            try {
                if (ShoppingList.getDAO().fillShoppingListWithProducts(new ShoppingList(shoppingListName)).isEmpty())
                    return;
            } catch (ShoppingListException e) {
                ExceptionManager.getInstance().handleException(new ErrorException(
                        "Erreur lors du calcule des produits dans une shoppinglist d'un magasin", e));
            }
            Store cheapest = null;
            try {
                cheapest = Store.getCheapestStore(shoppingLists.get(shoppingListName));
            } catch (StoreException e) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Une erreur est survenue lors de la récupération du magasin", e));
            }
            if (cheapest != null) {
                fx_mapWebengine.executeScript("setCheapest(" + cheapest.getLat() + ", " + cheapest.getLng() + ")");
                marketSelected(cheapest.getName());
            } else
                fx_errorLabelSl.setText(errorNotMarketForShoppingList);
        }

    }

    /**
     * method that adds the store to the list of stores
     */
    public void addStoreList(String name) {
        var btn = new MarketButton(name, this);
        fx_productListMap.getChildren().add(btn);
        fx_productScrollMap.setVvalue(fx_productScrollMap.getVmax() + btn.getPrefHeight());
    }

    /**
     * method that shows informations on a store (content, price, ...)
     *
     * @param storeName
     *            : name of the store
     */
    public void showStoreInfoWindow(String storeName) {
        MarketInfo marketInfo;
        try {
            marketInfo = new MarketInfo(storeName);
        } catch (ProductException | StoreException e) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Erreur lors du chargement du magasin", e));
            return;
        }
        Scene storeInfoScene = new Scene(marketInfo);
        Stage storeInfoWindow = new Stage();
        storeInfoWindow.setTitle("Informations de " + storeName);
        storeInfoWindow.setScene(storeInfoScene);
        Stage currentStage = SceneManager.getInstance().getMainStage();
        storeInfoWindow.setX(currentStage.getX() + currentStage.getWidth());
        storeInfoWindow.setY(currentStage.getY());
        storeInfoWindow.show();
    }

    /**
     * method that deletes a given store
     *
     * @param storeName
     *            : name of the store
     *
     * @throws StoreException
     *             : throws an exception when no store is found
     */
    public void deleteMarket(String storeName) throws StoreException {
        deleteMarketList(storeName);
        Store store = Store.getDAO().getStore(storeName);
        assert store != null;
        Store.getDAO().deleteStore(store);
        fx_mapWebengine.executeScript("deleteStore(" + store.getLat() + ", " + store.getLng() + ")");
    }

    /**
     * Method that adds a store to the side list
     *
     * @param storeName
     *            : name of the store we want to add
     */
    public void deleteMarketList(String storeName) {
        for (var i = 0; i < fx_productListMap.getChildren().size(); i++) {
            if (fx_productListMap.getChildren().get(i) instanceof MarketButton btn) {
                if (Objects.equals(btn.getText(), storeName)) {
                    fx_productListMap.getChildren().remove(i);
                    return;
                }
            }
        }
    }

    /**
     * method that shows the error to the user
     *
     * @param error
     *            : text of the error to show
     */
    // Public because we need it in the bridge
    public void setError(String error) {
        fx_errorLabelSl.setText(error);
    }

    /**
     * Name of the store Highlight the selected market
     *
     * @param storeName
     *            Name of the store
     */
    public void marketSelected(String storeName) {
        for (var i = 0; i < fx_productListMap.getChildren().size(); i++) {
            if (fx_productListMap.getChildren().get(i) instanceof MarketButton btn) {
                if (Objects.equals(btn.getText(), storeName)) {
                    // Set view on the store element in scrollPane
                    Animation animation = new Timeline(new KeyFrame(Duration.seconds(0.5),
                            new KeyValue(fx_productScrollMap.vvalueProperty(), (btn.getPrefHeight() * i) / 500)));
                    animation.play();
                    btn.highlightBtn();
                } else {
                    btn.removeHighlight();
                }
            }
        }
    }

    /**
     * method that fills the content of the shopping list, in the shopping list content box
     */
    private void fillContentShoppingList() throws ShoppingListException {
        fx_shoppingListList.getChildren().clear();
        var currentShoppingList = shoppingLists.get(fx_ComboBoxShoppingList.getValue());
        currentShoppingList = ShoppingList.getDAO().fillShoppingListWithProducts(currentShoppingList);

        if (!currentShoppingList.isEmpty())
            setShoppingListContent(shoppingListBoxTitle);
        else
            setShoppingListContent("");
        for (var product : currentShoppingList.getProducts().entrySet()) {
            var shoppingListElem = new ShoppingListItem(product.getKey(), product.getValue());
            fx_shoppingListList.getChildren().add(shoppingListElem);
        }
    }

    /**
     * method that sets the shoppingListName of the shopping list in the shopping list content box
     *
     * @param shoppingListName
     *            : name of the shopping list
     */
    private void setShoppingListContent(String shoppingListName) {
        if (!Objects.equals(shoppingListName, "")) {
            fx_shoppingListLabel.setText(shoppingListName);
            fx_shoppingListLabel.setStyle(
                    "-fx-background-color: #fff; -fx-border-style: hidden hidden solid hidden; -fx-border-width: .5; -fx-border-color: #212121;");
            fx_shoppingListContent.setStyle(
                    "-fx-background-color: #fff; " + "-fx-border-radius: 10px; " + "-fx-background-radius: 10px; "
                            + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 10, 0, 0, 0);");
        } else {
            fx_shoppingListLabel.setText("");
            fx_shoppingListLabel.setStyle("-fx-background-color: transparent");
            fx_shoppingListContent.setStyle("-fx-background-color: transparent");
        }
    }

    /**
     * marketSelected(name); centers the map on the Store when clicked on the store in the side list
     */
    public void onMarketClicked(String storeName) throws StoreException {
        marketSelected(storeName);
        var store = Store.getDAO().getStore(storeName);
        assert store != null;
        fx_mapWebengine.executeScript(
                "selectStore(\"" + store.getName() + "\", " + store.getLat() + ", " + store.getLng() + ")");
    }

}
