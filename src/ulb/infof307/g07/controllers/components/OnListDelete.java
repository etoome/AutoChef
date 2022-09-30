package ulb.infof307.g07.controllers.components;

import java.util.List;

@FunctionalInterface
public interface OnListDelete<T> {
    List<T> execute();
}
