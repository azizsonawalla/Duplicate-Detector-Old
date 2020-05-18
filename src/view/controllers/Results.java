package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import model.searchModel.searchStrategies.MetadataStrategy;
import org.jetbrains.annotations.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.DuplicateDetectorGUIApp;
import view.util.FormatConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static view.util.FormatConverter.milliSecondsToTime;
import static view.util.FormatConverter.sensibleDiskSpaceValue;

public class Results extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Scan results";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Results for";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "Found %d duplicate sets";

    /* UI controls */

    public Results(DuplicateDetectorGUIApp app) {
        super(app, null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
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

    @Override
    protected void cleanupSelf() {
        // TODO:
    }

    private Node loadMainWindow() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/Results.fxml"));                         // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            ScrollPane s = (ScrollPane) rootChildren.get(1);                                                            // TODO: replace all FXML child access from index to id
            GridPane g = (GridPane) s.getContent();
            GridPane results = createResultsPane(model.getResults());
            GridPane.setRowIndex(results, 0);
            GridPane.setColumnIndex(results, 1);
            g.getChildren().add(results);

            return root;
        } catch (Exception e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private List<List<File>> getDummyResults() {                                                                        // TODO: remove this
        List<List<File>> res = new LinkedList<>();
        File f = new File("D:\\Coding Projects\\Duplicate-Detector\\src\\view\\assets\\sample_image.jpeg");

        for (int i=0; i < 5; i++) {
            List<File> innerRes = new LinkedList<>();
            for (int j = 0; j < 5; j++) {
                innerRes.add(f);
            }
            res.add(innerRes);
        }

        return res;
    }

    private GridPane createResultsPane(List<List<File>> duplicateSets) {
        GridPane g = new GridPane();

        int maxDupsInASet = 0;
        for (List<File> set: duplicateSets) {
            maxDupsInASet = Math.max(maxDupsInASet, set.size());
        }

        g = addColumnConstraints(maxDupsInASet, g);
        g = addRowConstraints(duplicateSets.size(), g);

        ObservableList<Node> children = g.getChildren();
        for (int i = 0; i < duplicateSets.size(); i++) {
            int colIdx = 0;

            Label setNumPane = createSetNumberPane(i+1);
            GridPane.setRowIndex(setNumPane, i);
            GridPane.setColumnIndex(setNumPane, colIdx);
            children.add(setNumPane);
            colIdx++;

            for (File file: duplicateSets.get(i)) {
                GridPane previewPane = createFilePreviewPane(file);
                GridPane.setColumnIndex(previewPane, colIdx);
                GridPane.setRowIndex(previewPane, i);
                children.add(previewPane);
                colIdx++;
            }
        }

        return g;
    }

    private GridPane addColumnConstraints(int maxDupsInASet, @NotNull GridPane g) {
        // add constraint for number pane
        g.getColumnConstraints().add(new ColumnConstraints(100,100,100));

        // add preview column constraints
        for (int i = 0; i < maxDupsInASet; i++) {
            g.getColumnConstraints().add(new ColumnConstraints(300,300,300));
        }

        return g;

    }

    private GridPane addRowConstraints(int numOfDupSets, @NotNull GridPane g) {
        for (int i = 0; i < numOfDupSets; i++) {
            g.getRowConstraints().add(new RowConstraints(450, 450, 450));
        }
        return g;
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

        Pane imagePane = createImagePreviewPane(file.toURI().toString());
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

    private Pane createImagePreviewPane(String fileURI) {                                                               // TODO: create low res preview temp file
        Pane p = new Pane();
        GridPane.setRowIndex(p, 0);
        String bgCss = String.format("-fx-background-image: url(\"%s\");", fileURI);                                     // TODO: might have to be relative path  // TODO: might have to be async
        p.setStyle(bgCss + "-fx-background-position: center; -fx-background-size: cover;");
        return p;
    }

    private String createLastModifiedString(long lastModified) {
        String template = "Modified: %s ago";
        String time = milliSecondsToTime(System.currentTimeMillis() - lastModified);
        return String.format(template, time);
    }
}
