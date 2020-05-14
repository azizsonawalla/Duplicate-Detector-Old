package view.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import model.searchModel.ScanController;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.io.IOException;

public class NewScan extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Start a new scan";
    private String MAIN_CONTENT_TITLE = "Choose a folder to scan:";
    private String NEXT_BUTTON_TEXT = "Next";

    private String SUMMARY_BAR_TITLE_HEADER_DEFAULT = "No folder selected";
    private String FILE_PATH_DEFAULT = SUMMARY_BAR_TITLE_HEADER_DEFAULT;
    private String SUMMARY_BAR_TITLE_PREVIEW_DEFAULT = "";
    private String SUMMARY_BAR_SUBTITLE_DEFAULT = "Choose a folder to begin a scan";

    private String SUMMARY_BAR_TITLE_HEADER_SELECTED = "Chosen Folder";
    private String SUMMARY_BAR_SUBTITLE_SELECTED = "Click next to begin pre-scan checks.";

    /* UI controls */
    private Label filePathLabel;
    private Button browseButton;

    private File chosenDirectory;

    public NewScan(DuplicateDetectorGUIApp app) {
        super(app, null);                                                                                               // this is the first scene so prev scene is always null
    }

    void configureControls() {
        hideBackButton();
        hideCancelButton();
        disableNextButton();
        browseButton.setOnAction(this::openFileChooserAndDisplaySelectedPath);
    }

    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);

        if (chosenDirectory != null) {
            setChosenDirectory(this.chosenDirectory);
        } else {
            setSummaryBarTitle(SUMMARY_BAR_TITLE_HEADER_DEFAULT, SUMMARY_BAR_TITLE_PREVIEW_DEFAULT, false, false);
            setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_DEFAULT);
            filePathLabel.setText(FILE_PATH_DEFAULT);
        }
    }

    Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/NewScan.fxml"));              // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();
            AnchorPane filePathBox = (AnchorPane) rootChildren.get(0);
            ObservableList<Node> filePathBoxChildren = filePathBox.getChildren();
            this.filePathLabel = (Label) filePathBoxChildren.get(0);                                                    // save references to UI elements
            this.browseButton = (Button) filePathBoxChildren.get(1);
            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
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
            setChosenDirectory(this.chosenDirectory);
        }
    }

    private void setChosenDirectory(File dir) {
        String path = dir.getAbsolutePath();
        filePathLabel.setText(path);
        setSummaryBarTitle(SUMMARY_BAR_TITLE_HEADER_SELECTED, dir.getAbsolutePath(), true, true);
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_SELECTED);
        prepareNextScene(dir);
        enableNextButton();
    }

    private void prepareNextScene(File dir) {
        ScanController model = new ScanController(dir);
        app.setModel(model);
        setNextController(new PrepareToScan(app, this));
    }
}