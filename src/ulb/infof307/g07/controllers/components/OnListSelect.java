package ulb.infof307.g07.controllers.components;

@FunctionalInterface
public interface OnListSelect<T> {
    void execute(T object);
}
