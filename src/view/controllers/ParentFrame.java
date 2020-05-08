package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class ParentFrame implements Initializable {

    @FXML
    ImageView logo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image logoImage = new Image("view/assets/logo.png");                                                        // TODO: make all paths OS agnostic
        logo.setImage(logoImage);
    }
}
