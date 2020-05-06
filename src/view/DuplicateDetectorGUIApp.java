package view;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;


public class DuplicateDetectorGUIApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws MalformedURLException {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("layouts/FileChooser.fxml"));                   // TODO: replace with static config reference
        } catch (IOException e) {
            e.printStackTrace();
        }

        double screenCoverPercentage = 0.6;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int minDim = Math.min(screen.width, screen.height);
        Scene scene = new Scene(root, minDim*screenCoverPercentage, minDim*screenCoverPercentage);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}
