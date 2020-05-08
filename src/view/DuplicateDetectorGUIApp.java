package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;


public class DuplicateDetectorGUIApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("layouts/CommonFrame.fxml"));                           // TODO: replace with static config reference
        } catch (IOException e) {
            e.printStackTrace();
        }

        double screenCoverPercentage = 0.6;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int minDim = Math.min(screen.width, screen.height);
        Scene scene = new Scene(root, 1536, 864);

        stage.setMinWidth(400);
        stage.setMinHeight(400);
        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
