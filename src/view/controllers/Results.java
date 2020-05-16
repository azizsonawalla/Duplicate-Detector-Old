package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.searchModel.searchStrategies.MetadataStrategy;
import view.DuplicateDetectorGUIApp;

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
            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }
}
