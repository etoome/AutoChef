package ulb.infof307.g07.controllers.shoppinglist;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.SVGPath;
import javafx.util.Pair;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.controllers.components.ListEditableController;
import ulb.infof307.g07.controllers.components.search.SearchController;
import ulb.infof307.g07.controllers.layout.HeaderController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.models.recipe.IngredientUnit;
import ulb.infof307.g07.models.shoppinglist.*;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.shoppinglist.exceptions.ShoppingListException;
import ulb.infof307.g07.utils.Colors;
import ulb.infof307.g07.utils.Delay;
import ulb.infof307.g07.utils.Icons;
import ulb.infof307.g07.utils.Now;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainController implements Initializable, BaseController {

    @FXML
    private TilePane fx_selection;

    @FXML
    private TilePane fx_cart;

    @FXML
    private SVGPath fx_export;

    @FXML
    private SVGPath fx_archive;

    @FXML
    private SVGPath fx_save;

    @FXML
    private HeaderController headerController;

    @FXML
    private SearchController searchController;

    @FXML
    private ListEditableController listeditableController;

    private List<Product> products = new ArrayList<>();
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private ShoppingList currentShoppingList;

    private final PDFExporter pdfExporter = new PDFExporter();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        searchController.set(this::search);

        try {
            products = Product.getDAO().getAllProducts();
        } catch (ProductException exception) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("La liste de tout les produits n'a pas pu être chargée ", exception));
        }
        try {
            shoppingLists = ShoppingList.getDAO().getAllShoppingLists();
        } catch (ShoppingListException exception) {
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("La shopping list n'a pas pu être chargée", exception));
        }
        listeditableController.start(shoppingLists, (ShoppingList selected) -> {

            currentShoppingList = selected;

            renderSelection();
            renderCart();
            renderComponents();

        }, (String name) -> {

            try {
                currentShoppingList.updateName(name);
            } catch (IllegalArgumentException exception) {
                ExceptionManager.getInstance().handleException(new ErrorException("Nom invalide", exception));
            } catch (ShoppingListException exception) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Le nom de la shopping liste n'a pas etre modifiee", exception));
            }

            return shoppingLists;
        }, () -> {

            String name = new Now().toString();

            try {
                currentShoppingList = ShoppingList.getDAO().createShoppingList(name);
                shoppingLists.add(currentShoppingList);
            } catch (ShoppingListException exception) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("La liste ne peut pas etre créée", exception));
            }

            return shoppingLists;
        }, () -> {
            try {
                ShoppingList.getDAO().deleteShoppingList(currentShoppingList.getName());
                shoppingLists.remove(currentShoppingList);
            } catch (ShoppingListException exception) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("La liste ne peut pas etre suprimée", exception));
            }

            return shoppingLists;
        });
    }

    @FXML
    private void OnArchive() {
        if (currentShoppingList == null)
            return;

        fx_archive.setFill(Colors.DISABLE.getColor());
        Delay.wait(500, () -> {
            if (fx_archive.getFill() != Colors.ERROR.getColor()) {
                fx_archive.setFill(Colors.ENABLE.getColor());
            }
        });

        if (currentShoppingList.isArchived()) {
            try {
                currentShoppingList.unarchive();
            } catch (ShoppingListException exception) {
                fx_archive.setFill(Colors.ERROR.getColor());
                ExceptionManager.getInstance().handleException(
                        new ErrorException("La shopping liste n'a pas pu etre de-archivée", exception));

            }
        } else {
            try {
                currentShoppingList.archive();
            } catch (ShoppingListException exception) {
                fx_archive.setFill(Colors.ERROR.getColor());
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("La shopping liste n'a pas pu etre archivée", exception));
            }
        }
        renderComponents();
        listeditableController.refresh();
        renderSelection();
    }

    @FXML
    private void OnExport() {
        fx_export.setFill(Colors.DISABLE.getColor());
        Delay.wait(500, () -> {
            if (fx_export.getFill() != Colors.ERROR.getColor()) {
                fx_export.setFill(Colors.ENABLE.getColor());
            }
        });

        try {
            pdfExporter.export(currentShoppingList);
        } catch (IOException exception) {
            fx_export.setFill(Colors.ERROR.getColor());
            ExceptionManager.getInstance().handleException(new ErrorException("Erreur pendant l'export", exception));
        }
    }

    @FXML
    private void OnSave() {
        fx_save.setFill(Colors.DISABLE.getColor());
        Delay.wait(500, () -> {
            if (fx_save.getFill() != Colors.ERROR.getColor()) {
                fx_save.setFill(Colors.ENABLE.getColor());
            }
        });

        try {
            ShoppingList.getDAO().save(currentShoppingList);
        } catch (ShoppingListException exception) {
            fx_save.setFill(Colors.ERROR.getColor());
            ExceptionManager.getInstance()
                    .handleException(new ErrorException("Erreur pendant la sauvegarde", exception));
        }

    }

    private void search(List<Pair<String, String>> values) {
        fx_selection.getChildren().clear();
        Product.Category[] categories = Product.Category.values();
        for (Product.Category category : categories) {
            for (Product product : products) {
                if (product.getCategory() == category) {
                    for (Pair<String, String> value : values) {
                        if (Objects.equals(value.getKey(), "")) {
                            Pattern pattern = Pattern.compile(value.getValue(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(product.getName());
                            if (matcher.find()) {
                                float quantity = 0F;
                                try {
                                    quantity = currentShoppingList.getProductQuantityValue(product);
                                } catch (ShoppingListException ignored) {
                                }
                                loadProduct(product, quantity);
                            }
                        }
                    }
                }
            }
        }
    }

    private void renderComponents() {
        if (currentShoppingList.isArchived()) {
            fx_archive.setContent(Icons.EDIT.getPath());
        } else {
            fx_archive.setContent(Icons.ARCHIVE.getPath());
        }
    }

    private void renderSelection() {
        fx_selection.getChildren().clear();

        if (currentShoppingList.isArchived()) {
            fx_selection.setPrefColumns(1);
            fx_selection.getChildren().add(new ArchivedController(currentShoppingList, () -> {
                try {
                    currentShoppingList.unarchive();
                } catch (ShoppingListException exception) {
                    fx_archive.setFill(Colors.ERROR.getColor());
                    ExceptionManager.getInstance().handleException(
                            new ErrorException("La shopping liste n'a pas pu etre de-archivée", exception));

                }

                renderComponents();
                listeditableController.refresh();
                renderSelection();
            }));
        } else {
            Product.Category[] categories = Product.Category.values();
            for (Product.Category category : categories) {
                for (Product product : products) {
                    if (product.getCategory() == category) {
                        float quantity = 0F;
                        for (Product productInShoppingList : currentShoppingList.getProducts().keySet()) {
                            if (product.equals(productInShoppingList)) {
                                try {
                                    quantity = currentShoppingList.getProductQuantityValue(productInShoppingList);
                                } catch (ShoppingListException ignored) {
                                }
                                break;
                            }
                        }
                        loadProduct(product, quantity);
                    }
                }
            }
        }
    }

    private void renderCart() {
        fx_cart.getChildren().clear();
        Product.Category[] categories = Product.Category.values();
        for (Product.Category category : categories) {
            for (var element : currentShoppingList.getProducts().entrySet()) {
                if (element.getKey().getCategory() == category) {
                    loadCartProduct(element.getKey(), element.getValue().getQuantity(), element.getValue().getUnit());
                }
            }
        }
    }

    private void loadProduct(Product product, Float quantity) {
        fx_selection.setPrefColumns(3);
        fx_selection.getChildren().add(new ProductController(product, quantity, this::productChange));
    }

    private void productChange(Product product, Float quantity) {

        if (currentShoppingList.hasProduct(product)) {
            try {
                if (quantity == 0) { // If qty is null or negative : we remove the product in the shoppingList
                    currentShoppingList.removeProduct(product);
                } else if (quantity > 0) { // Else we set the quantity q in the product
                    currentShoppingList.setProductQuantity(product, quantity);
                }
            } catch (ShoppingListException exception) {
                ExceptionManager.getInstance()
                        .handleException(new ErrorException("Inpossible de mettre a jour le produit", exception));
            }
        } else {
            if (quantity > 0) {
                currentShoppingList.addProduct(product, quantity);
            }
        }
        renderCart();
    }

    private void loadCartProduct(Product product, Float quantity, IngredientUnit unit) {
        fx_cart.getChildren().add(new CartProductController(product, quantity, unit));
    }
}
