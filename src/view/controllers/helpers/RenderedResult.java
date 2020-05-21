package view.controllers.helpers;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.StackPane;

import java.io.File;

/**
 * A reference to the rendered version of a search result
 */
public class RenderedResult {

    private File file;
    private StackPane previewPane;
    private CheckBox checkBox;

    public RenderedResult(File file, StackPane previewPane, CheckBox checkBox) {
        this.file = file;
        this.previewPane = previewPane;
        this.checkBox = checkBox;
    }

    public File getFile() {
        return file;
    }

    public StackPane getPreviewPane() {
        return previewPane;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
