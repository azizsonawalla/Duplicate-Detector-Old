package view.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseFolderToScan extends ParentController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Start a new scan";
    private String SUMMARY_BAR_TITLE_HEADER = "No folder selected";
    private String FILE_PATH_DEFAULT = SUMMARY_BAR_TITLE_HEADER;
    private String SUMMARY_BAR_TITLE_PREVIEW = "";
    private String SUMMARY_BAR_SUBTITLE = "0 files found";
    private String MAIN_CONTENT_TITLE = "Choose a folder to scan:";

    /* UI controls */
    private Label filePathLabel;
    private Button browseButton;

    private File chosenDirectory;

    public ChooseFolderToScan(DuplicateDetectorGUIApp app) {
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

        browseButton.setOnAction(this::openFileChooserAndDisplaySelectedPath);
        setNextButtonText("Next");
        // TODO: set action for next button
    }

    private Node getMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/ChooseFolderToScanContent.fxml")); // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            AnchorPane filePathBox = (AnchorPane) rootChildren.get(0);
            ObservableList<Node> filePathBoxChildren = filePathBox.getChildren();

            this.filePathLabel = (Label) filePathBoxChildren.get(0);                                                    // save references to UI elements
            this.browseButton = (Button) filePathBoxChildren.get(1);

            return root;
        } catch (IOException e) {
            e.printStackTrace(); // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void openFileChooserAndDisplaySelectedPath(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (this.chosenDirectory != null) {
            directoryChooser.setInitialDirectory(this.chosenDirectory);
        }
        this.chosenDirectory = directoryChooser.showDialog(app.getStage());

        if (this.chosenDirectory != null) {
            String path = this.chosenDirectory.getAbsolutePath();
            filePathLabel.setText(path);
            setSummaryBarTitle("Chosen Folder", this.chosenDirectory.getAbsolutePath(), true, true);
            setSummaryBarSubtitle("Click next to begin pre-scan checks.");
            enableNextButton();
        } else {
            disableNextButton();
            filePathLabel.setText(FILE_PATH_DEFAULT);
        }
    }
}
