package view.controllers.helpers;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import util.Logger;
import view.util.FXMLUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static util.ImageUtil.createLowResTemp;
import static view.textBindings.ResultsText.FAILED_TO_LOAD_PREVIEW_MESSAGE;

/**
 * A runnable class to load image previews and inject them into rendered results
 */
public class ImagePreviewLoader implements Runnable {

    private Pane pane;
    private File file;
    private Logger log;

    private static String BACKGROUND_CSS_TEMPLATE = "-fx-background-image: url(\"%s\");";
    private static List<String> CSS_CLASSES_FOR_SUCCESSFUL_LOAD = Arrays.asList("previewPane");
    private static List<String> CSS_CLASSES_FOR_FAILED_LOAD = Arrays.asList("erroredImagePreview", "previewPane");
    private static List<String> CSS_CLASSES_FOR_FAILED_MSG = Arrays.asList("body");

    public ImagePreviewLoader(File file, Pane pane) {
        this.pane = pane;
        this.file = file;
        this.log = new Logger(this.getClass());
    }

    /**
     * Loads image preview and injects it, or injects a failure message if an error occurs.
     */
    @Override
    public void run() {
        try {
            File temp = createLowResTemp(file, -1, 450);
            String fileURI = temp.toURI().toString();
            String css = String.format(BACKGROUND_CSS_TEMPLATE, fileURI);
            FXMLUtils.addStyling(css, CSS_CLASSES_FOR_SUCCESSFUL_LOAD, pane, true);
        } catch (Exception e) {
            log.error("Failed to load image preview for " + file.getAbsolutePath() + ": " + e.getMessage());
            FXMLUtils.addStyling("", CSS_CLASSES_FOR_FAILED_LOAD, pane, true);
            Platform.runLater(() -> pane.getChildren().add(getLabelForFailedLoad()));
        }
    }

    /**
     * Creates a label to denote a failed preview load
     * @return a label with an error message
     */
    private static Label getLabelForFailedLoad() {
        Label failedMessage = new Label(FAILED_TO_LOAD_PREVIEW_MESSAGE);
        failedMessage.getStyleClass().addAll(CSS_CLASSES_FOR_FAILED_MSG);
        return failedMessage;
    }
}
