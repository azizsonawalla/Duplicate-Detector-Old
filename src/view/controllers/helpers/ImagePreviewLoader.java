package view.controllers.helpers;

import javafx.scene.layout.Pane;

import java.io.File;

import static util.ImageUtil.createLowResTemp;

public class ImagePreviewLoader implements Runnable {

    private Pane pane;
    private File file;

    public ImagePreviewLoader(File file, Pane pane) {
        this.pane = pane;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            File temp = createLowResTemp(file, -1, 450);
            String fileURI = temp.toURI().toString();
            String css = String.format("-fx-background-image: url(\"%s\");", fileURI);
            css += "-fx-background-position: center; -fx-background-size: cover;";
            pane.getStyleClass().clear();
            pane.setStyle(css);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO:
        }
    }
}
