package view;

import config.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.async.threadPool.AppThreadPool;
import model.searchModel.ScanController;
import view.controllers.GUIController;
import view.controllers.NewScan;
import view.util.dialogues.AppErrorDialogue;

import java.io.IOException;


/**
 * GUI application entrypoint for Duplicate Detector
 */
public class DuplicateDetectorGUIApp extends Application {

    private Stage stage;
    private ScanController model;

    /**
     * Launches the application
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        GUIController controller = new NewScan(this);
        try {
            Scene firstScene = loadDefaultScene(controller);
            this.stage = configureDefaultStage(stage, firstScene);
            this.stage.show();
        } catch (IOException e) {
            // TODO: log
            stop();
        }
    }

    /**
     * Get the application stage
     * @return the primary stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Get the underlying model
     * @return ScanController model instance
     */
    public ScanController getModel() {
        return model;
    }

    /**
     * Set the underlying application model
     * @param model a ScanController instance
     */
    public void setModel(ScanController model) {
        this.model = model;
    }

    /**
     * Switch from the current scene to another scene. Will initialize a scene with the parent layout and pass the given
     * controller to it.
     * @param newController controller for the new scene
     */
    public void switchScene(GUIController newController) {
        runWithWaitCursor(() -> {
            Scene newScene = null;
            try {
                newScene = loadSceneOfSameSize(this.stage.getScene(), newController);
            } catch (IOException e) {
                e.printStackTrace();
                AppErrorDialogue.showError("An error occurred while loading the next page. Please restart the application.");
            }
            this.stage.setScene(newScene);
        });
    }

    public void runWithWaitCursor(Runnable func) {
        Cursor ogCursor = stage.getScene().getCursor();
        Platform.runLater(() -> stage.getScene().setCursor(Cursor.WAIT));
        Platform.runLater(func);
        Platform.runLater(() -> stage.getScene().setCursor(ogCursor));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        AppThreadPool.getInstance().shutdownNow();
    }


    /**
     * Same as loadDefaultScene() except the dimensions of the new scene are taken from the given scene.
     * @param s scene to use for dimensions
     * @param c controller for new scene
     * @return a new scene with the same dimensions as s
     * @throws IOException if an error occurs while creating the scene
     */
    private Scene loadSceneOfSameSize(Scene s, GUIController c) throws IOException {
        double width = s.getWidth();
        double height = s.getHeight();
        return loadScene(c, Config.PARENT_FRAME, Config.DARK_THEME_CSS, width, height);
    }

    private Scene loadDefaultScene(GUIController c) throws IOException {
        return loadScene(c, Config.PARENT_FRAME, Config.DARK_THEME_CSS, Config.SCENE_WIDTH, Config.SCENE_HEIGHT );
    }

    private Scene loadScene(GUIController c, String fxml, String css, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));                                               // TODO: store loader in-memory to speed-up scene switching
        loader.setController(c);
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
        return scene;
    }

    private Stage configureDefaultStage(Stage stage, Scene scene) {                                                     // TODO: add stage and taskbar icons
        stage.setMinWidth(Config.STAGE_MIN_WIDTH);
        stage.setMinHeight(Config.STAGE_MIN_HEIGHT);
        stage.setTitle(Config.STAGE_TITLE);

        stage.setScene(scene);
        return stage;
    }
}
