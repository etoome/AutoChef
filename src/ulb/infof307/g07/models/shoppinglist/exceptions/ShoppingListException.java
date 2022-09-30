package ulb.infof307.g07.models.shoppinglist.exceptions;

public class ShoppingListException extends Exception {

    public ShoppingListException(String message) {
        super(message);
    }

    public ShoppingListException(String message, Throwable cause) {
        super(message, cause);
    }
}
