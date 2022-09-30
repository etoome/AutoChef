package ulb.infof307.g07;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ulb.infof307.g07.managers.scene.SceneManager;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("images/icon_background.png"))));
        var sceneManager = SceneManager.createInstance(stage, 1280, 720);
        sceneManager.setMainScene("recipe/main.fxml");
        sceneManager.getMainStage().show();
    }

    public static void main(String[] args) {
        launch();
    }
}
