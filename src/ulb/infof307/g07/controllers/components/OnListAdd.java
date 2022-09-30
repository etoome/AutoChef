package ulb.infof307.g07.controllers.components;

import java.util.List;

@FunctionalInterface
public interface OnListAdd<T> {
    List<T> execute();
}
