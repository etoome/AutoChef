package ulb.infof307.g07.models.recipe.exceptions;

public class IngredientException extends Exception {

    public IngredientException(String msg) {
        super(msg);
    }

    public IngredientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
