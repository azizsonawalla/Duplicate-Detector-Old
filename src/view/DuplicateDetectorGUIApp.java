package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controllers.ChooseFolderToScan;
import view.controllers.ParentController;

import java.awt.*;
import java.io.IOException;


public class DuplicateDetectorGUIApp extends Application {

    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {                                                                                    // TODO: cleanup
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/ParentFrame.fxml"));               // TODO: replace with static config reference
            loader.setController(new ChooseFolderToScan(this));
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        double screenCoverPercentage = 0.6;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int minDim = Math.min(screen.width, screen.height);
        Scene scene = new Scene(root, 1536, 864);
        scene.getStylesheets().add(getClass().getResource("layouts/darkTheme.css").toExternalForm());

        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setTitle("FXML Welcome"); // TODO: replace
        stage.setScene(scene);
        stage.show();
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
}
