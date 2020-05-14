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
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

public class ConfigureScan extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Configure scan";
    private String MAIN_CONTENT_TITLE = "Choose a scan type";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Scanning";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "%d files will be inspected";

    /* UI controls */
    private GridPane quickScan, fullScan, advScan;

    ConfigureScan(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();

        List<GridPane> allStrategyButtons = Arrays.asList(quickScan, fullScan, advScan);
        allStrategyButtons.forEach((GridPane g) -> g.setOnMouseClicked(this::setStrategyAndGoToNextScene));
    }

    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);

        long totalFileCount = model.getProgress().getDone();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFileCount));
    }

    Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/ConfigureScan.fxml"));                  // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();
            quickScan = (GridPane) rootChildren.get(0);
            fullScan = (GridPane) rootChildren.get(1);
            advScan = (GridPane) rootChildren.get(2);
            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void setStrategyAndGoToNextScene(MouseEvent e) {                                                            // TODO: add remaining strategies when created
        Object source = e.getSource();
        if (source.equals(quickScan)) {
            model.setStrategy(new MetadataStrategy());
        } else {
            throw new InvalidParameterException("Couldn't identify strategy for button");
            // TODO: error handling
        }

        createAndSetNextController();
        goToNextScene();
    }

    private void createAndSetNextController() {
        RunScan rs = new RunScan(app, this);
        setNextController(rs);
    }
}
