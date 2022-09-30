package ulb.infof307.g07.controllers.shoppinglist;

import javafx.stage.FileChooser;
import ulb.infof307.g07.App;
import ulb.infof307.g07.managers.scene.SceneManager;
import ulb.infof307.g07.models.shoppinglist.ShoppingList;

import java.io.File;
import java.io.IOException;

// https://www.tutorialspoint.com/itext/itext_overview.htm
public class PDFExporter {
    public static class Constants {
        public static final String PDF_HEADER = "Liste de courses: ";
        public static final String PDF_TABLE_HEADER_PRODUCT_TITLE = "Nom du produit";
        public static final String PDF_TABLE_HEADER_QUANTITY_TITLE = "Quantité";
        public static final float TABLE_QUANTITY_WIDTH = 100f;
        public static final int LOGO_SIZE = 100;
        public static final int CATEGORY_SIZE = 50;
        public static final String LOGO_IMG_PATH = String.valueOf(App.class.getResource("images/icon_background.png"));
        public static final String BAKERY_IMG_PATH = String.valueOf(App.class.getResource("images/icon_bakery.png"));
        public static final String CARB_IMG_PATH = String.valueOf(App.class.getResource("images/icon_carb.png"));
        public static final String DAIRY_IMG_PATH = String.valueOf(App.class.getResource("images/icon_dairy.png"));
        public static final String DRINK_IMG_PATH = String.valueOf(App.class.getResource("images/icon_drink.png"));
        public static final String FISH_IMG_PATH = String.valueOf(App.class.getResource("images/icon_fish.png"));
        public static final String FRUIT_IMG_PATH = String.valueOf(App.class.getResource("images/icon_fruit.png"));
        public static final String MEAT_IMG_PATH = String.valueOf(App.class.getResource("images/icon_meat.png"));
        public static final String PREPARED_IMG_PATH = String
                .valueOf(App.class.getResource("images/icon_prepared.png"));
        public static final String SAUCE_IMG_PATH = String.valueOf(App.class.getResource("images/icon_sauce.png"));
        public static final String SNACK_IMG_PATH = String.valueOf(App.class.getResource("images/icon_snack.png"));
        public static final String SPICE_IMG_PATH = String.valueOf(App.class.getResource("images/icon_spice.png"));
        public static final String TOOL_IMG_PATH = String.valueOf(App.class.getResource("images/icon_tool.png"));
        public static final String VEGETABLE_IMG_PATH = String
                .valueOf(App.class.getResource("images/icon_vegetable.png"));
    }

    private final ulb.infof307.g07.models.shoppinglist.PDFExporter pdfExporterModel = new ulb.infof307.g07.models.shoppinglist.PDFExporter();

    public void export(ShoppingList shoppinglist) throws IOException {
        // open a modal window to chose destination of export
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        var sceneManager = SceneManager.getInstance();
        File file = fileChooser.showSaveDialog(sceneManager.getMainStage());
        if (file != null) {
            pdfExporterModel.export(shoppinglist, file);
        }
    }
}
