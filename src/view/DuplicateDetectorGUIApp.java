package view;

import config.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.searchModel.ScanController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.controllers.ChooseFolderToScan;

import java.io.IOException;


public class DuplicateDetectorGUIApp extends Application {

    private Stage stage;
    private ScanController model;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Initializable controller = new ChooseFolderToScan(this);
        try {
            Scene firstScene = loadScene (
                                        controller,
                                        Config.PARENT_FRAME_PATH,
                                        Config.DARK_THEME_CSS_PATH,
                                        Config.SCENE_WIDTH,
                                        Config.SCENE_HEIGHT
                                );
            this.stage = configureStage(stage, firstScene);
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

    public void switchScene(Initializable newController) {
        // TODO
        throw new NotImplementedException();
    }

    private Scene loadScene(Initializable cntrl, String fxml, String css, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setController(cntrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource(css).toExternalForm());
        return scene;
    }

    private Stage configureStage(Stage stage, Scene scene) {
        stage.setMinWidth(400);                                                                                         // TODO: replace with static config reference
        stage.setMinHeight(400);                                                                                        // TODO: replace with static config reference
        stage.setTitle("FXML Welcome");                                                                                 // TODO: replace with static config reference
        stage.setScene(scene);
        return stage;
    }
}
