package ulb.infof307.g07.models.recipe;

import java.util.Arrays;
import java.util.Optional;

public enum RecipeStyle {
    MEAT("Viande"), FISH("Poisson"), VEGGIE("Végé"), VEGAN("Vegan");

    private final String name;

    RecipeStyle(String typeName) {
        this.name = typeName;
    }

    /**
     * Get value from string, who's a typeName, else return null
     */
    public static RecipeStyle getValue(String typeName) {
        Optional<RecipeStyle> rec = Arrays.stream(RecipeStyle.values()).filter(rv -> rv.name.equals(typeName))
                .findFirst();
        return rec.orElse(null);
    }

    @Override
    public String toString() {
        return name;
    }
}
