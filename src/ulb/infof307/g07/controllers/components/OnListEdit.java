package ulb.infof307.g07.controllers.components;

import java.util.List;

@FunctionalInterface
public interface OnListEdit<T> {
    List<T> execute(String name);
}
