package view.controllers.helpers;

import javafx.application.Platform;
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
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static view.util.FormatConverter.milliSecondsToTime;
import static view.util.FormatConverter.sensibleDiskSpaceValue;

/**
 * Renders search results into an FXML format
 */
public class ResultsRenderer {

    private static Logger log = new Logger(ResultsRenderer.class);

    public static List<List<RenderedResult>> addResultsToResultsPane(List<List<File>> results, GridPane resPane, int start, int end) {

        List<List<RenderedResult>> renderedResults = new LinkedList<>();

        ObservableList<Node> children = resPane.getChildren();
        ObservableList<RowConstraints> rConstraints = resPane.getRowConstraints();
        for (int i = start; i <= end; i++) {

            rConstraints.add(new RowConstraints(450, 450, 450));
            int colIdx = 0;

            Label setNumPane = createSetNumberPane(i+1);
            GridPane.setRowIndex(setNumPane, i);
            GridPane.setColumnIndex(setNumPane, colIdx);
            children.add(setNumPane);
            colIdx++;

            List<RenderedResult> resultSet = new LinkedList<>();
            for (File file: results.get(i)) {
                CheckBox checkBox = createImageCheckBox();
                StackPane imgPreviewPane = createImagePreviewPane(checkBox);
                GridPane filePreviewPane = createFilePreviewPane(file, imgPreviewPane);

                GridPane.setColumnIndex(filePreviewPane, colIdx);
                GridPane.setRowIndex(filePreviewPane, i);

                children.add(filePreviewPane);
                colIdx++;

                resultSet.add(new RenderedResult(file, imgPreviewPane, checkBox));
            }
            renderedResults.add(resultSet);
        }

        return renderedResults;
    }

    public static void disableResultPanes(List<RenderedResult> results) {
        for (RenderedResult res: results) {
            Pane pane = res.getPreviewPane();
            pane.setDisable(true);
        }
    }

    private static Label createSetNumberPane(int setNo) {
        Label l = new Label(Integer.toString(setNo));
        l.setOpacity(0.5);
        l.getStyleClass().add("h2");
        GridPane.setHalignment(l, HPos.LEFT);
        GridPane.setValignment(l, VPos.CENTER);
        return l;                                                                                                       // TODO: col and row index set by caller
    }

    private static GridPane createFilePreviewPane(File file, StackPane imagePane) {                                     // TODO: move all constants to config

        if (!file.isFile()) {
            throw new InvalidParameterException("File is invalid");                                                     // TODO: error handling
        }

        GridPane g = new GridPane();
        g.setMaxWidth(300);
        g.setMaxHeight(300);                                                                                             // TODO: col and row index will be set by caller

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(90);
        g.getColumnConstraints().add(c);

        for (int i = 0; i < 5; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(6);
            r.setMaxHeight(0.07*300);
            g.getRowConstraints().add(r);
        }
        RowConstraints r = new RowConstraints();
        r.setPercentHeight(70);
        r.setMaxHeight(0.7*300);
        g.getRowConstraints().add(0, r);

        List<Label> labels = createImagePreviewDetails(file);

        ObservableList<Node> children = g.getChildren();                                                                // TODO: replace with calls to FXML util functions
        for (int j = 0; j < labels.size(); j++) {
            GridPane.setRowIndex(labels.get(j), j+2);
            labels.get(j).getStyleClass().add("details");
            children.add(labels.get(j));
        }
        labels.get(0).getStyleClass().clear();
        labels.get(0).getStyleClass().add("body");

        GridPane.setRowIndex(imagePane, 0);
        children.add(0, imagePane);

        return g;
    }

    private static List<Label> createImagePreviewDetails(File file) {
        String name = file.getName();
        String parent = file.getParentFile().getName();
        String size = sensibleDiskSpaceValue(file.length());
        String lastModified = createLastModifiedString(file.lastModified());

        return Arrays.asList(
                new Label(name),
                new Label(parent),
                new Label(size),
                new Label(lastModified)
        );
    }

    private static StackPane createImagePreviewPane(CheckBox cb) {
        StackPane sp = new StackPane();
        sp.getChildren().add(cb);
        sp.getStyleClass().add("defaultImagePreview");
        GridPane.setRowIndex(sp, 0);
        return sp;
    }

    private static CheckBox createImageCheckBox() {
        CheckBox cb = new CheckBox();
        cb.setSelected(false);
        cb.getStyleClass().add("resultsCheckBox");
        StackPane.setAlignment(cb, Pos.TOP_RIGHT);
        return cb;
    }

    private static String createLastModifiedString(long lastModified) {
        String template = "Modified: %s ago";
        String time = milliSecondsToTime(System.currentTimeMillis() - lastModified);
        return String.format(template, time);
    }
}
