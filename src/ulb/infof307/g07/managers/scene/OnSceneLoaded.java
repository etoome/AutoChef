package ulb.infof307.g07.managers.scene;

import javafx.scene.Scene;
import ulb.infof307.g07.controllers.BaseController;

/**
 * Signature of the lambda action that is used by the SceneManager
 */
@FunctionalInterface
public interface OnSceneLoaded {
    void execute(Scene scene, BaseController controller);
}
