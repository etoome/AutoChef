package ulb.infof307.g07.controllers;

/**
 * Necessary interface that all controllers need to implement, if they are to be initialized inside a SceneManager's
 * action
 */
public interface BaseController {
    default <T extends BaseController> T getController() {
        // noinspection unchecked
        return (T) this;
    }
}
