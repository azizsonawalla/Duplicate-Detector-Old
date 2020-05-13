package view.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.async.threadPool.AppThreadPool;
import model.searchModel.ScanController;
import model.util.Progress;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseStrategy extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Configure scan";
    private String MAIN_CONTENT_TITLE = "Choose a scan type";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Scanning";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "%d files will be inspected";

    /* UI controls */

    private ScanController model;

    public ChooseStrategy(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        model = app.getModel();
        setContent(loadMainContent());
        initCopy();
        configureControls();
    }

    private void configureControls() {
        hideNextButton();
        hideCancelButton();
    }

    private void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);

        long totalFileCount = model.getProgress().getDone();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFileCount));

        File chosenFolder = model.getRootDirectories().get(0);
        setSummaryBarTitle(SUMMARY_BAR_HEADER_DEFAULT, chosenFolder.getAbsolutePath(), true, true);                     // TODO: move to parent
    }

    private Node loadMainContent() {
//        try {
//            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/PreScanChecks.fxml"));                   // TODO: replace with static config reference
//
//            ObservableList<Node> rootChildren = root.getChildren();
//            this.filePathLabel = (Label) rootChildren.get(0);
//            this.fileCountLabel = (Label) rootChildren.get(2);
//
//            StackPane stackPane = (StackPane) rootChildren.get(1);
//            ObservableList<Node> stackPaneChildren = stackPane.getChildren();
//            this.progressBar = (ProgressBar) stackPaneChildren.get(0);
//            this.completeLabel = (Label) stackPaneChildren.get(1);
//
//            return root;
//        } catch (IOException e) {
//            e.printStackTrace();                                                                                        // TODO: error handling
//        }
        return new Label("Error loading content");
    }

    private void createAndSetNextController() {
        ChooseStrategy c = new ChooseStrategy(app, this);
        setNextController(c);
    }
}
