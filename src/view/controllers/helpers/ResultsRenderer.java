package view.controllers.helpers;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import util.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static view.util.FormatConverter.milliSecondsToTime;
import static view.util.FormatConverter.sensibleDiskSpaceValue;

/**
 * Helper functions to render search results into FXML nodes
 */
public class ResultsRenderer {

    private static Logger log = new Logger(ResultsRenderer.class);

    /**
     * Renders and adds the given results (from start to end indices) into the resPane
     * @param results results from a search
     * @param resPane GridPane to inject rendered FXML nodes into
     * @param start index of the result to start rendering from
     * @param end index of the result to stop rendering at (inclusive)
     * @return references to the rendered results as a 2D list of RenderedResult. Shape corresponds to results passed.
     */
    public static List<List<RenderedResult>> addResultsToResultsPane (
            List<List<File>> results,
            GridPane resPane,
            int start,
            int end
    ) {

        List<List<RenderedResult>> renderedResults = new LinkedList<>();
        ObservableList<Node> children = resPane.getChildren();
        ObservableList<RowConstraints> rConstraints = resPane.getRowConstraints();

        for (int i = start; i <= end; i++) {
            int colIdx = 0;
            rConstraints.add(new RowConstraints(450, 450, 450));
            Label setNumPane = createSetNumberLabel(i+1, i, colIdx);
            children.add(setNumPane);
            colIdx++;

            List<RenderedResult> resultSet = new LinkedList<>();
            for (File file: results.get(i)) {
                CheckBox checkBox = createResultCheckBox();
                StackPane imgPreviewPane = createResultPreview(checkBox);
                List<Label> fileDetails = createResultDetails(file, 2);
                GridPane filePreviewPane = createResultPane(fileDetails, imgPreviewPane, i, colIdx);
                children.add(filePreviewPane);
                resultSet.add(new RenderedResult(file, imgPreviewPane, checkBox));
                colIdx++;
            }
            renderedResults.add(resultSet);
        }

        return renderedResults;
    }

    /**
     * Disable the preview panes for all the given results
     * @param results results to disable
     */
    public static void disableResultPanes(List<RenderedResult> results) {
        for (RenderedResult res: results) {
            Pane pane = res.getPreviewPane();
            pane.setDisable(true);
        }
    }

    /**
     * Creates label for numbering a result set
     * @param setNo set number of the result set
     * @param rowIdx row index of result set in parent pane
     * @param colIdx column index for set number in parent pane
     * @return label for set number
     */
    private static Label createSetNumberLabel(int setNo, int rowIdx, int colIdx) {
        Label l = new Label(Integer.toString(setNo));
        l.setOpacity(0.5);
        l.getStyleClass().add("h2");
        GridPane.setHalignment(l, HPos.LEFT);
        GridPane.setValignment(l, VPos.CENTER);
        GridPane.setRowIndex(l, rowIdx);
        GridPane.setColumnIndex(l, colIdx);
        return l;
    }

    /**
     * Renders the FXML result Pane for the given file using the image preview pane and file details labels
     * @param details labels containing file details
     * @param previewPane image preview pane for the file
     * @param rowIdx row index for rendered pane
     * @param colIdx column index for rendered pane
     * @return rendered result pane
     */
    private static GridPane createResultPane(List<Label> details, StackPane previewPane, int rowIdx, int colIdx) {

        GridPane resultPane = new GridPane();
        resultPane.setMaxWidth(300);
        resultPane.setMaxHeight(300);
        GridPane.setColumnIndex(resultPane, colIdx);
        GridPane.setRowIndex(resultPane, rowIdx);

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(90);
        resultPane.getColumnConstraints().add(c);

        for (int i = 0; i < 5; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(6);
            r.setMaxHeight(0.07*300);
            resultPane.getRowConstraints().add(r);
        }
        RowConstraints r = new RowConstraints();
        r.setPercentHeight(70);
        r.setMaxHeight(0.7*300);
        resultPane.getRowConstraints().add(0, r);

        ObservableList<Node> children = resultPane.getChildren();
        children.addAll(details);

        GridPane.setRowIndex(previewPane, 0);
        children.add(0, previewPane);

        return resultPane;
    }

    /**
     * Creates labels containing file details
     * @param file file object associate with result
     * @return labels for file details
     */
    private static List<Label> createResultDetails(File file, int rowIdxOffset) {
        Label nameLabel = new Label(file.getName());
        Label parentLabel = new Label(file.getParentFile().getName());
        Label sizeLabel = new Label(sensibleDiskSpaceValue(file.length()));
        Label lastModLabel = new Label(createLastModifiedString(file.lastModified()));

        nameLabel.getStyleClass().add("body");
        parentLabel.getStyleClass().add("details");
        sizeLabel.getStyleClass().add("details");
        lastModLabel.getStyleClass().add("details");

        List<Label> details = Arrays.asList(nameLabel, parentLabel, sizeLabel, lastModLabel);

        for (int j = 0; j < details.size(); j++) {
            GridPane.setRowIndex(details.get(j), j+rowIdxOffset);
        }

        return details;
    }

    /**
     * Creates the preview pane for the file. This function does not inject the preview itself, it just creates the
     * container. Preview is injected at asynchronously.
     * @param cb checkbox to layer on top of preview pane
     * @return preview pane for file
     */
    private static StackPane createResultPreview(CheckBox cb) {
        StackPane sp = new StackPane();
        sp.getChildren().add(cb);
        sp.getStyleClass().add("defaultImagePreview");
        GridPane.setRowIndex(sp, 0);
        return sp;
    }

    /**
     * Create the checkbox for the result pane
     * @return an FXML checkbox to be added to the result pane
     */
    private static CheckBox createResultCheckBox() {
        CheckBox cb = new CheckBox();
        cb.setSelected(false);
        cb.getStyleClass().add("resultsCheckBox");
        StackPane.setAlignment(cb, Pos.TOP_RIGHT);
        return cb;
    }

    /**
     * Converts milliseconds duration to an easy to read time duration phrase
     * @param lastModified milliseconds since the last file modification
     * @return string form of the duration
     */
    private static String createLastModifiedString(long lastModified) {
        String template = "Modified: %s ago";
        String time = milliSecondsToTime(System.currentTimeMillis() - lastModified);
        return String.format(template, time);
    }
}
