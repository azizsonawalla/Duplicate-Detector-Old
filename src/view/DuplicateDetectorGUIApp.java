package view;

import config.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.searchModel.ScanController;
import view.controllers.ChooseFolderToScan;
import view.controllers.GUIController;
import view.controllers.PreScanChecks;
import view.util.AppError;

import java.io.IOException;


public class DuplicateDetectorGUIApp extends Application {

    private Stage stage;
    private ScanController model;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GUIController controller = new ChooseFolderToScan(this);
        try {
            Scene firstScene = loadDefaultScene(controller);
            this.stage = configureDefaultStage(stage, firstScene);
            this.stage.show();
        } catch (IOException e) {
            // TODO: shutdown
        }
    }

    public Stage getStage() {
        return stage;
    }

    public ScanController getModel() {
        return model;
    }

    public void setModel(ScanController model) {
        this.model = model;
    }

    public void switchScene(GUIController newController) {
        Scene newScene = null;
        try {
            newScene = loadDefaultScene(newController);
        } catch (IOException e) {
            e.printStackTrace();
            AppError.showError("An error occurred while loading the next page. Please restart the application.");
        }
        this.stage.setScene(newScene);
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
