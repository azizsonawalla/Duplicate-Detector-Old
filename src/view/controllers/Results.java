package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import java.util.List;
import java.util.ResourceBundle;

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
//        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);

//        long totalFileCount = model.getProgress().getDone();
//        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFileCount));
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }

    private Node loadMainWindow() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/Results.fxml"));                         // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();
            // TODO: implement
            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
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
            g.getRowConstraints().add(new RowConstraints(350, 350, 350));
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

    private GridPane createFilePreviewPane(File file) {
        String name = file.getName();
        String parent = file.getParent();
        String size = Long.toString(file.length());
        String lastModified = FormatConverter.milliSecondsToTime(System.currentTimeMillis() - file.lastModified()) + " ago";

        GridPane g = new GridPane();
        g.setMaxWidth(300);
        g.setMaxWidth(300);                                                                                             // TODO: col and row index will be set by caller

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(90);
        g.getColumnConstraints().add(c);

        for (int i = 0; i < 4; i++) {
            RowConstraints r = new RowConstraints();
            r.setPercentHeight(7);
            g.getRowConstraints().add(r);
        }
        RowConstraints r = new RowConstraints();
        r.setPercentHeight(70);
        g.getRowConstraints().add(0, r);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("body");
        Label parentLabel = new Label(parent);
        parentLabel.setStyle("body");
        Label sizeLabel = new Label(size);
        sizeLabel.setStyle("body");
        Label modifiedLabel = new Label(lastModified);
        modifiedLabel.setStyle("body");

        Pane imagePane = createImagePreviewPane(file.getAbsolutePath());

        ObservableList<Node> children = g.getChildren();
        children.add(0, imagePane);
        children.add(1, nameLabel);
        children.add(2, parentLabel);
        children.add(3, sizeLabel);
        children.add(4, modifiedLabel);

        return g;
    }

    private Pane createImagePreviewPane(String absPath) {
        Pane p = new Pane();
        GridPane.setRowIndex(p, 0);
        p.prefWidth(300);
        p.prefHeight(300);

        String bgCss = String.format("-fx-background-image: url(\"%s\")", absPath);                                     // TODO: might have to be relative path  // TODO: might have to be async
        p.setStyle(bgCss);
        return p;
    }


}
