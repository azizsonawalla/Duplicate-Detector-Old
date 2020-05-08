package view.controllers;

import javafx.fxml.Initializable;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;

public class ParentFrame implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFonts();
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("../assets/fonts/HelveticaNeueLTCom_Lt.ttf"), 16);
    }
}
