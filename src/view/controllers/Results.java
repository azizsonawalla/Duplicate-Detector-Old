package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import model.async.threadPool.AppThreadPool;
import org.jetbrains.annotations.NotNull;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.*;

import static util.ImageUtil.createLowResTemp;
import static view.util.FormatConverter.milliSecondsToTime;
import static view.util.FormatConverter.sensibleDiskSpaceValue;

public class Results extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Scan results";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Results for";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "Found %d duplicate sets";
    private String LOAD_BUTTON_TEXT = "Load more results";

    /* UI controls */
    private GridPane resultsPane;
    private Button loadMore;
    private Map<File, Pane> imagePreviewPanes = new HashMap<>();
    private int renderedResults = 0;

    /* Model data */
    private List<List<File>> results;

    /* Other Constants */
    private int RESULT_GROUP_SIZE = 10;

    Results(DuplicateDetectorGUIApp app) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        results = model.getResults();
        resultsPane = createResultsPane(results);
        setMainWindow(loadMainWindow());
    }

    @Override
    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();
    }

    @Override
    void initCopy() {
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);

        long duplicateCount = model.getProgress().getPositives();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, duplicateCount));
    }

    private GridPane createResultsPane(List<List<File>> results) {
        GridPane resultsPane = new GridPane();

        int maxDupsInASet = 0;
        for (List<File> set: results) {
            maxDupsInASet = Math.max(maxDupsInASet, set.size());
        }

        addResultsPaneColumnConstraints(maxDupsInASet, resultsPane);
        return resultsPane;
    }

    private Node loadMainWindow() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/Results.fxml"));                         // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            ScrollPane s = (ScrollPane) rootChildren.get(1);                                                            // TODO: replace all FXML child access from index to id
            GridPane g = (GridPane) s.getContent();
            loadMore = (Button) g.getChildren().get(0);
            loadMore.setOnAction(event -> loadNextSetOfResults());                                                      // TODO: move this to configure controls (needs to be called after loadMainWindow though)

            GridPane.setRowIndex(resultsPane, 0);
            GridPane.setColumnIndex(resultsPane, 1);
            g.getChildren().add(resultsPane);

            loadNextSetOfResults();

            return root;
        } catch (Exception e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void loadNextSetOfResults() {
        int startIdx = renderedResults;
        int endIdx = Math.min(renderedResults + RESULT_GROUP_SIZE, results.size()-1);
        addResultsToResultsPane(results, resultsPane, startIdx, endIdx);
        loadImagePreviews(imagePreviewPanes);
        renderedResults += RESULT_GROUP_SIZE;
        GridPane.setRowIndex(loadMore, renderedResults);
    }

    private void addResultsToResultsPane(List<List<File>> results, GridPane resultsPane, int startIdx, int endIdx) {

        int numOfResultsToAdd = endIdx - startIdx + 1;
        addResultsPaneRowConstraints(numOfResultsToAdd, resultsPane);

        ObservableList<Node> children = resultsPane.getChildren();
        for (int i = startIdx; i <= endIdx; i++) {
            int colIdx = 0;

            Label setNumPane = createSetNumberPane(i+1);
            GridPane.setRowIndex(setNumPane, i);
            GridPane.setColumnIndex(setNumPane, colIdx);
            children.add(setNumPane);
            colIdx++;

            for (File file: results.get(i)) {
                GridPane previewPane = createFilePreviewPane(file);
                GridPane.setColumnIndex(previewPane, colIdx);
                GridPane.setRowIndex(previewPane, i);
                children.add(previewPane);
                colIdx++;
            }
        }
    }

    private void loadImagePreviews(Map<File, Pane> previewPanes) {
        log.debug("Creating preview loading threads");
        for (Map.Entry<File, Pane> entry: previewPanes.entrySet()) {
            AppThreadPool.getInstance().submit(new LoadImagePreviews(entry.getKey(), entry.getValue()));
        }
        log.debug("Done creating preview loading threads");
    }

    private void addResultsPaneColumnConstraints(int maxSetSize, @NotNull GridPane g) {
        // add constraint for number pane
        g.getColumnConstraints().add(new ColumnConstraints(100,100,100));

        // add preview column constraints
        for (int i = 0; i < maxSetSize; i++) {
            g.getColumnConstraints().add(new ColumnConstraints(300,300,300));
        }
    }

    private void addResultsPaneRowConstraints(int numOfRows, @NotNull GridPane g) {
        for (int i = 0; i < numOfRows; i++) {
            g.getRowConstraints().add(new RowConstraints(450, 450, 450));
        }
    }

    private Label createSetNumberPane(int setNo) {
        Label l = new Label(Integer.toString(setNo));
        l.setOpacity(0.5);
        l.getStyleClass().add("h2");
        GridPane.setHalignment(l, HPos.LEFT);
        GridPane.setValignment(l, VPos.CENTER);
        return l;                                                                                                       // TODO: col and row index set by caller
    }

    private GridPane createFilePreviewPane(File file) {                                                                 // TODO: move all constants to config

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

        Pane imagePane = createImagePreviewPane(file);
        GridPane.setRowIndex(imagePane, 0);
        children.add(0, imagePane);

        return g;
    }

    private List<Label> createImagePreviewDetails(File file) {
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

    private Pane createImagePreviewPane(File file) {
        Pane p = new Pane();
        GridPane.setRowIndex(p, 0);
        imagePreviewPanes.put(file, p);
        return p;
    }

    private String createLastModifiedString(long lastModified) {
        String template = "Modified: %s ago";
        String time = milliSecondsToTime(System.currentTimeMillis() - lastModified);
        return String.format(template, time);
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }

    private class LoadImagePreviews implements Runnable {

        private Pane pane;
        private File file;

        LoadImagePreviews(File file, Pane pane) {
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
                pane.setStyle(css);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO:
            }
        }
    }
}
