package ulb.infof307.g07.models.recipe;

import java.util.Arrays;
import java.util.Optional;

// All types of recipe
public enum RecipeType {
    ENTREE("Entrée"), MEAL("Plat"), DESSERT("Dessert"), DRINK("Boisson"), STEWED("Mijoté"), SOUP("Soupe");

    private final String name;

    RecipeType(String typeName) {
        this.name = typeName;
    }

    /**
     * Get value from string, who's a typeName, else return null
     */
    public static RecipeType getValue(String typeName) {
        Optional<RecipeType> rt = Arrays.stream(RecipeType.values()).filter(rec -> rec.name.equals(typeName))
                .findFirst();
        return rt.orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
