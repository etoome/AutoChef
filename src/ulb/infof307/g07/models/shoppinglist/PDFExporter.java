package ulb.infof307.g07.models.shoppinglist;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;

import static ulb.infof307.g07.controllers.shoppinglist.PDFExporter.Constants.*;

// https://www.tutorialspoint.com/itext/itext_overview.htm
public class PDFExporter {

    public void export(ShoppingList shoppinglist, File file) throws IOException {
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.addNewPage();
        Document document = new Document(pdfDocument);

        var docPageWidth = pdfDocument.getDefaultPageSize().getWidth();

        addLogo(document);

        document.add(new Paragraph(PDF_HEADER + shoppinglist.getName()).setBold().setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20f)); // title

        for (Product.Category category : Product.Category.values()) {
            boolean tableCreated = false;
            boolean emptyTable = true;
            float[] pointColumnWidths = { (docPageWidth - TABLE_QUANTITY_WIDTH), TABLE_QUANTITY_WIDTH };
            Table table = new Table(pointColumnWidths);
            for (var productCategoryPair : shoppinglist.getProducts().entrySet()) {
                if (productCategoryPair.getKey().getCategory() == category) {
                    if (!tableCreated) {
                        document.add(new Paragraph("\n")); // add blank lines
                        document.add(
                                new Paragraph(category.getValue()).setBold().setTextAlignment(TextAlignment.CENTER)); // add
                                                                                                                      // category
                                                                                                                      // name
                        document.add(addCategoryImage(category)); // add category image
                        tableCreated = true;
                    }
                    if (emptyTable) {
                        // adding header cells
                        table.addHeaderCell(new Cell().add(new Paragraph(PDF_TABLE_HEADER_PRODUCT_TITLE)).setBold());
                        table.addHeaderCell(new Cell().add(new Paragraph(PDF_TABLE_HEADER_QUANTITY_TITLE)).setBold());
                        emptyTable = false;
                    }
                    // create and add cell to table
                    var productName = productCategoryPair.getKey().getName();
                    var quantity = (int) productCategoryPair.getValue().getQuantity() + " "
                            + productCategoryPair.getValue().getUnit().getUnitString();
                    table.addCell(new Cell().add(new Paragraph(productName)));
                    table.addCell(new Cell().add(new Paragraph(quantity)));
                }
            }
            document.add(table);
        }

        document.close();
        writer.close();
    }

    private void addLogo(Document document) throws IOException {
        ImageData data = ImageDataFactory.create(LOGO_IMG_PATH);
        Image image = new Image(data);
        image.scaleAbsolute(LOGO_SIZE, LOGO_SIZE);
        image.setFixedPosition(480, 730); // upper right corner
        document.add(image);

        document.add(new Paragraph("\n\n\n\n")); // blank lines
        document.add(new Paragraph("by AutoChef").setTextAlignment(TextAlignment.RIGHT).setFontSize(8f));
    }

    private Image addCategoryImage(Product.Category category) throws IOException {
        switch (category) {
        case BAKERY -> {
            ImageData data_bakery = ImageDataFactory.create(BAKERY_IMG_PATH);
            Image image_bakery = new Image(data_bakery);
            image_bakery.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_bakery.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_bakery;
        }
        case CARB -> {
            ImageData data_carb = ImageDataFactory.create(CARB_IMG_PATH);
            Image image_carb = new Image(data_carb);
            image_carb.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_carb.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_carb;
        }
        case DAIRY -> {
            ImageData data_dairy = ImageDataFactory.create(DAIRY_IMG_PATH);
            Image image_dairy = new Image(data_dairy);
            image_dairy.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_dairy.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_dairy;
        }
        case DRINK -> {
            ImageData data_drink = ImageDataFactory.create(DRINK_IMG_PATH);
            Image image_drink = new Image(data_drink);
            image_drink.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_drink.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_drink;
        }
        case FISH -> {
            ImageData data_fish = ImageDataFactory.create(FISH_IMG_PATH);
            Image image_fish = new Image(data_fish);
            image_fish.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_fish.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_fish;
        }
        case FRUIT -> {
            ImageData data_fruit = ImageDataFactory.create(FRUIT_IMG_PATH);
            Image image_fruit = new Image(data_fruit);
            image_fruit.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_fruit.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_fruit;
        }
        case MEAT -> {
            ImageData data_meal = ImageDataFactory.create(MEAT_IMG_PATH);
            Image image_meal = new Image(data_meal);
            image_meal.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_meal.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_meal;
        }
        case PREPARED -> {
            ImageData data_prepared = ImageDataFactory.create(PREPARED_IMG_PATH);
            Image image_prepared = new Image(data_prepared);
            image_prepared.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_prepared.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_prepared;
        }
        case SAUCE -> {
            ImageData data_sauce = ImageDataFactory.create(SAUCE_IMG_PATH);
            Image image_sauce = new Image(data_sauce);
            image_sauce.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_sauce.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_sauce;
        }
        case SNACK -> {
            ImageData data_snack = ImageDataFactory.create(SNACK_IMG_PATH);
            Image image_snack = new Image(data_snack);
            image_snack.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_snack.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_snack;
        }
        case SPICE -> {
            ImageData data_spice = ImageDataFactory.create(SPICE_IMG_PATH);
            Image image_spice = new Image(data_spice);
            image_spice.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_spice.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_spice;
        }
        case TOOL -> {
            ImageData data_tool = ImageDataFactory.create(TOOL_IMG_PATH);
            Image image_tool = new Image(data_tool);
            image_tool.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_tool.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_tool;
        }
        case VEGETABLE -> {
            ImageData data_vegetable = ImageDataFactory.create(VEGETABLE_IMG_PATH);
            Image image_vegetable = new Image(data_vegetable);
            image_vegetable.scaleAbsolute(CATEGORY_SIZE, CATEGORY_SIZE);
            image_vegetable.setHorizontalAlignment(HorizontalAlignment.CENTER);
            return image_vegetable;
        }
        }
        return null;
    }
}
