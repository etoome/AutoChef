package ulb.infof307.g07.controllers.components.search.popup;

public enum State {
    ON("on"), OFF("off");

    private final String value;

    State(String value) {
        this.value = value;
    }

    public String getState() {
        return value;
    }
}
