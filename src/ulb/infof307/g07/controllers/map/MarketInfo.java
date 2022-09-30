package ulb.infof307.g07.controllers.map;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ulb.infof307.g07.database.Database;
import ulb.infof307.g07.database.DatabaseException;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.ErrorException;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.shoppinglist.Product;
import ulb.infof307.g07.models.shoppinglist.exceptions.ProductException;
import ulb.infof307.g07.models.store.Store;
import ulb.infof307.g07.models.store.exceptions.StoreException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MarketInfo extends VBox {

    @FXML
    private Label fx_storeName;
    @FXML
    private ScrollPane fx_productListScroll;
    @FXML
    private TextField fx_nameProductInput;
    @FXML
    private TextField fx_priceProductInput;
    @FXML
    private ComboBox<String> fx_comboCategory;
    @FXML
    private Label fx_labelError;

    public MarketInfo(String name) throws ProductException, StoreException {
        SceneManager.getInstance().loadFxmlNode(this, "map/add_product.fxml");
        fx_storeName.setText(name);
        fx_comboCategory.setItems(Product.Category.getObservableListOfCategory());
        loadProducts();
    }

    @FXML
    private void addProductInMarket() {
        if (!Objects.equals(fx_nameProductInput.getText(), "") && !Objects.equals(fx_priceProductInput.getText(), "")) {
            try {
                Product product = new Product(fx_nameProductInput.getText(),
                        Product.Category.getValue(fx_comboCategory.getValue()),
                        Float.parseFloat(fx_priceProductInput.getText()));
                Store store = Store.getDAO().getStore(fx_storeName.getText());
                if (!Store.getDAO().isProductInStore(product, store)) {
                    Product.getDAO().createProduct(product);
                    Store.getDAO().addProductInStore(product, store);
                    setError("");
                } else {
                    setError("Produit déjà présent");
                }
                loadProducts();
            } catch (NumberFormatException exception) {
                setError("Prix incorrect");
            } catch (StoreException | ProductException exception) {
                ExceptionManager.getInstance().handleException(
                        new ErrorException("Erreur lors du rajout d'un produit dans le magasin", exception));
            }
        } else {
            setError("Entrée(s) incorrecte(s)");
        }
    }

    public File csvFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier à importer");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showOpenDialog(this.getScene().getWindow());
    }

    public List<String[]> parseCSVfile(File file) throws IOException {
        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        List<String[]> allProducts;
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(file)).withCSVParser(parser).build()) {
            allProducts = reader.readAll();
        }
        return allProducts;
    }

    public boolean hasCSVFileGoodFormat(File file) {
        if (file == null)
            return false;
        try {
            var allProducts = parseCSVfile(file);
            for (String[] product : allProducts) {
                if (product.length != 3)
                    return false;
                Float.parseFloat(product[2]); // The third colum contains a float value
                Product.Category.valueOf(product[1]); // The category is ok
            }
        } catch (Exception e) {
            return false; // checks failed or io error
        }
        return true;
    }

    @FXML
    private void OnImport() {
        try {
            var db = Database.getInstance();
            Store store = Store.getDAO().getStore(fx_storeName.getText());

            File file = csvFileChooser();
            if (!hasCSVFileGoodFormat(file)) {
                setError("Fichier illisible");
                return;
            }
            List<String[]> allProducts = parseCSVfile(file);
            if (allProducts.size() > 0) {
                for (String[] productLine : allProducts) {
                    Product product = new Product(productLine[0], Product.Category.valueOf(productLine[1]));
                    if (!Product.getDAO().isProductAlreadyInDb(product))
                        Product.getDAO().createProduct(product);
                    if (!Store.getDAO().isProductInStore(product, store, Float.parseFloat(productLine[2]))) {
                        db.query("INSERT INTO StoreProduct (store_id, product_id, price) VALUES(?,?,?)",
                                List.of(Store.getDAO().getIdFromStore(store),
                                        Product.getDAO().getIdFromProduct(product), Float.parseFloat(productLine[2])));
                        loadProducts();
                    }
                }
            }
        } catch (StoreException | DatabaseException | ProductException | IOException e) {
            ExceptionManager.getInstance().handleException(
                    new ErrorException("Erreur lors de l'importation des produits dans le magasin", e));
        }

    }

    public void setStoreName(String name) {
        fx_storeName.setText(name);
    }

    public ScrollPane getListProduct() {
        return fx_productListScroll;
    }

    public void loadProducts() throws StoreException {
        VBox listproduct = new VBox();
        Product.Category currentCategory = null;
        for (Product product : Store.getDAO().getProductsInStore(fx_storeName.getText())) {
            if (currentCategory != product.getCategory()) {
                currentCategory = product.getCategory();
                Label category = new Label(currentCategory.getValue());
                category.setStyle("-fx-font-size: 13pt; -fx-font-weight: bold; -fx-text-fill: #212121;");
                category.setPadding(new Insets(5, 0, 0, 10));
                listproduct.getChildren().add(category);
            }
            ProductButton btn = new ProductButton(product.getName(), product.getPrice());
            listproduct.getChildren().add(btn);
        }
        fx_productListScroll.setContent(listproduct);
    }

    public void setError(String error) {
        fx_labelError.setText(error);
    }

}
