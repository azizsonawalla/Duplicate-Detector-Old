package view.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreScanChecks extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Preparing to scan";
    private String SUMMARY_BAR_TITLE_HEADER = "No folder selected";                                                     // TODO: take this from file passed from prev dialogue
    private String SUMMARY_BAR_TITLE_PREVIEW = "";                                                                      // TODO: take this from file passed from prev dialogue
    private String SUMMARY_BAR_SUBTITLE = "Run pre-scan checks to analyze folder";
    private String MAIN_CONTENT_TITLE = "Pre-scan check";

    /* UI controls */
    private Label filePathLabel;
    private Button browseButton;

    private File chosenDirectory;

    public PreScanChecks(DuplicateDetectorGUIApp app) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        setNavBarTitle(NAV_BAR_TITLE);

        setSummaryBarTitle(SUMMARY_BAR_TITLE_HEADER, SUMMARY_BAR_TITLE_PREVIEW, false, false);
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE);

        setContentTitle(MAIN_CONTENT_TITLE);

        hideBackButton();
        hideCancelButton();
        disableNextButton();

        setContent(getMainContent());

        setNextButtonText("Next");
    }

    private Node getMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/PreScanChecks.fxml"));             // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            AnchorPane filePathBox = (AnchorPane) rootChildren.get(0);
            ObservableList<Node> filePathBoxChildren = filePathBox.getChildren();

            this.filePathLabel = (Label) filePathBoxChildren.get(0);                                                    // save references to UI elements
            this.browseButton = (Button) filePathBoxChildren.get(1);

            return root;
        } catch (IOException e) {
            e.printStackTrace(); // TODO: error handling
        }
        return new Label("AppError loading content");
    }
}
