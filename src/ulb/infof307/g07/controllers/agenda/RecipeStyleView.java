package ulb.infof307.g07.controllers.agenda;

import ulb.infof307.g07.models.recipe.RecipeStyle;

public final class RecipeStyleView {

    private static RecipeStyleView instance;

    public static RecipeStyleView getInstance() {
        if (instance == null)
            instance = new RecipeStyleView();
        return instance;
    }

    private RecipeStyleView() {
    }

    public String getImgPath(RecipeStyle style) {
        return switch (style) {
        case MEAT -> "ulb/infof307/g07/images/meat.png";
        case FISH -> "ulb/infof307/g07/images/fish.png";
        case VEGGIE -> "ulb/infof307/g07/images/veggie.png";
        case VEGAN -> "ulb/infof307/g07/images/vegan.png";
        };
    }

    public String getColor(RecipeStyle style) {
        return switch (style) {
        case MEAT -> "#E25B45";
        case FISH -> "#89D5C9";
        case VEGGIE -> "#ADC965";
        case VEGAN -> "#00C853";
        };
    }
}
