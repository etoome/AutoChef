package ulb.infof307.g07.controllers.shoppinglist;

import ulb.infof307.g07.models.shoppinglist.Product;

@FunctionalInterface
public interface onProductClick {
    void execute(Product product, Float quantity);
}
