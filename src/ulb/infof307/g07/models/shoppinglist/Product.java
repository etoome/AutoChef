package ulb.infof307.g07.models.shoppinglist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ulb.infof307.g07.database.dao.DAO;
import ulb.infof307.g07.database.dao.product.ProductDAO;

import java.util.*;

/** Represents a product in the shopping list */
public class Product {
    private final String name;
    private final float price;
    private final Category category;

    public Product(String name, Category category) {
        this(name, category, 0);
    }

    public Product(String name, Category category, float price) {
        if (name == null) {
            throw new IllegalArgumentException("Product's name cannot be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("Product's category cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Product's price cannot be negative");
        }
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof Product product)) {
            return false;
        }

        return Objects.equals(this.name, product.name);
    }

    public static ProductDAO getDAO() {
        return DAO.getInstance().getProductDAO();
    }

    public boolean equals(Product other) {
        return Objects.equals(this.name, other.getName()) && this.category == other.getCategory()
                && this.price == other.getPrice();
    }

    public enum Category {
        VEGETABLE("Légumes"), SNACK("Friandises"), SAUCE("Sauces"), DAIRY("Produits laitiers"), TOOL("Ustensiles"),
        PREPARED("Préparations"), FISH("Poisson"), MEAT("Viande"), FRUIT("Fruits"), BAKERY("Boulangerie"),
        SPICE("Epices"), CARB("Glucides"), DRINK("Boissons");

        public static ObservableList<String> getObservableListOfCategory() {
            List<String> res = new ArrayList<>();
            for (var cat : Category.values())
                res.add(cat.getValue());
            return FXCollections.observableArrayList(res);
        }

        private final String value;

        Category(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        // Get value from string
        public static Category getValue(String typeName) {
            if (typeName == null)
                throw new IllegalArgumentException("Can't used a null string to get a value in category");
            Optional<Category> rec = Arrays.stream(Category.values()).filter(rv -> rv.value.equals(typeName))
                    .findFirst();
            return rec.orElse(null);
        }
    }

}