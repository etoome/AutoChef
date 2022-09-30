package ulb.infof307.g07.managers.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ulb.infof307.g07.controllers.BaseController;
import ulb.infof307.g07.managers.exception.ExceptionManager;
import ulb.infof307.g07.managers.exception.types.SilentException;

import java.io.IOException;

/**
 * Abstraction for everything that is related to switching Scenes or loading and attaching scenes to JavaFX Nodes This
 * class is a Singleton, but doesn't allow lazy initialization, creating the global instance needs to be done explicitly
 * at the begining of the application with {@link SceneManager#createInstance}
 */
public class SceneManager {
    private static SceneManager instance = null;
    private final Stage mainStage;
    private final int SCENE_WIDTH;
    private final int SCENE_HEIGHT;
    private final String RESOURCES_PATH_PREFIX = "/ulb/infof307/g07/";

    /**
     * Create the singleton instance of the SceneManager
     *
     * @param mainStage
     *            Stage used by the application, usually passed by the start method of
     *            {@link javafx.application.Application}, cannot be null.
     * @param sceneWidth
     *            Width of the window in pixels
     * @param sceneHeight
     *            Height of the window in pixels
     *
     * @return a ref of the newly created instance of the SceneManager
     */
    public static SceneManager createInstance(Stage mainStage, int sceneWidth, int sceneHeight)
            throws IllegalArgumentException {
        return instance = new SceneManager(mainStage, sceneWidth, sceneHeight);
    }

    /**
     * Get the singleton instance of the SceneManager
     *
     * @return the instance of the SceneManager
     */
    public static SceneManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("Instance of SceneManager wasn't created!");
        }
        return instance;
    }

    /**
     * Constructor of the SceneManager
     *
     * @param mainStage
     *            Stage of the application, usually given to the start method of {@link javafx.application.Application}.
     *            This field
     * @param sceneWidth
     *            Width of the window in pixels
     * @param sceneHeight
     *            Height of the window in pixels
     */
    private SceneManager(Stage mainStage, int sceneWidth, int sceneHeight) throws IllegalArgumentException {
        SCENE_WIDTH = sceneWidth;
        SCENE_HEIGHT = sceneHeight;
        if (mainStage == null) {
            throw new IllegalArgumentException("Cannot create SceneManager with no stage");
        }
        this.mainStage = mainStage;
    }

    public Scene getCurrentScene() {
        return this.mainStage.getScene();
    }

    public Stage getMainStage() {
        return this.mainStage;
    }

    private Scene loadScene(String url, int width, int height, OnSceneLoaded action) {
        Scene scene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESOURCES_PATH_PREFIX + url));
        try {
            Parent parent_node = loader.load();
            scene = new Scene(parent_node, width, height);
            if (action != null) {
                BaseController controller = loader.getController();
                action.execute(scene, controller);
            }
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
            throw new RuntimeException(exception);
        }
        return scene;
    }

    /**
     * Create an auxiliary stage to act as a sub-window of the application
     *
     * @param url
     *            the url of the scene
     * @param title
     *            of the new stage
     * @param action
     *            to be performed when the scene is loaded
     */
    public void createAuxiliaryStage(String url, String title, OnSceneLoaded action) {
        Scene scene = loadScene(url, SCENE_WIDTH / 2, SCENE_HEIGHT, action);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Set the main Scene.
     *
     * @param url
     *            URL of the fxml file, a path prefix is already taken into account. Check
     *            {@link SceneManager#RESOURCES_PATH_PREFIX}.
     * @param action
     *            lambda that provides the newly loaded scene (before it is shown on screen) and the
     *            {@link BaseController} that was loaded. Use {@link BaseController#getController()} to get the subtype
     *            of the controller.
     */
    public void setMainScene(String url, OnSceneLoaded action) {
        Scene scene = loadScene(url, SCENE_WIDTH, SCENE_HEIGHT, action);
        mainStage.setScene(scene);
    }

    /**
     * Overload of {@link SceneManager#setMainScene(String, OnSceneLoaded)}
     *
     * @see SceneManager#setMainScene(String, OnSceneLoaded)
     */
    public void setMainScene(String url) {
        setMainScene(url, null);
    }

    /**
     * Load an fxml file at url and attach it to the node. The node act as it's controller and root.
     *
     * @param node
     *            which will act as the controller for the loaded fxml
     * @param url
     *            the resource path for the fxml file
     */
    public void loadFxmlNode(Node node, String url) {
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(node.getClass().getResource(RESOURCES_PATH_PREFIX + url));
            loader.setController(node);
            loader.setRoot(node);
            loader.load();
        } catch (IOException e) {
            // silently fail to load UI, we trust the user will reload the app, or just switch scene
            ExceptionManager.getInstance()
                    .handleException(new SilentException("Erreur lors de la lecture d'un fichier FXML", e));
        }
    }
}
