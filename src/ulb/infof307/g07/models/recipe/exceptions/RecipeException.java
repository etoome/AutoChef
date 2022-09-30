package ulb.infof307.g07.models.recipe.exceptions;

public class RecipeException extends Exception {

    public RecipeException(String msg) {
        super(msg);
    }

    public RecipeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
