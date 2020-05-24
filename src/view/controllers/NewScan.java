package view.controllers;

import config.Config;
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
import view.textBindings.NewScanText;

import java.io.File;

import static view.util.FXMLUtils.getChildWithId;

/**
 * UI Controller for the NewScan scene
 */
public class NewScan extends GUIController {

    /* UI controls */
    private Label filePathLabel;
    private Button browseButton;
    private File chosenDirectory;

    /**
     * Create an instance of the NewScan controller
     * @param app instance of the JavaFX application associated with this controller
     */
    public NewScan(DuplicateDetectorGUIApp app) {
        super(app);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void configureControls() {
        hideBackButton();
        hideCancelButton();
        disableNextButton();
        browseButton.setOnAction(this::openFileChooserAndDisplaySelectedPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void initCopy() {
        setContentTitle(NewScanText.MAIN_CONTENT_TITLE);
        setNextButtonText(NewScanText.NEXT_BUTTON_TEXT);
        setNavBarTitle(NewScanText.NAV_BAR_TITLE);

        if (chosenDirectory != null) {
            setChosenDirectory(this.chosenDirectory);
        } else {
            setSummaryBarTitle(NewScanText.SUMMARY_BAR_TITLE_HEADER_DEFAULT, NewScanText.SUMMARY_BAR_TITLE_PREVIEW_DEFAULT, false, false);
            setSummaryBarSubtitle(NewScanText.SUMMARY_BAR_SUBTITLE_DEFAULT);
            filePathLabel.setText(NewScanText.FILE_PATH_DEFAULT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Node loadMainContent() throws Exception {
        GridPane root = FXMLLoader.load(getClass().getResource(Config.LAYOUTS_NEW_SCAN_FXML));
        AnchorPane filePathBox = (AnchorPane) getChildWithId(root, "filePathBox");
        this.filePathLabel = (Label) getChildWithId(filePathBox, "filePathLabel");
        this.browseButton = (Button) getChildWithId(filePathBox, "browseButton");
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cleanupSelf() {
        super.cleanupSelf();
        filePathLabel = null;
        browseButton = null;
        chosenDirectory = null;
    }

    /**
     * Let's the user select a directory and updates the UI with the selection
     * @param e action event from user input
     */
    private void openFileChooserAndDisplaySelectedPath(ActionEvent e) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (this.chosenDirectory != null) {
            directoryChooser.setInitialDirectory(this.chosenDirectory);
        }
        this.chosenDirectory = directoryChooser.showDialog(app.getStage());

        app.runWithWaitCursor(() -> {
            if (this.chosenDirectory != null) {
                setChosenDirectory(this.chosenDirectory);
            }
        });
    }

    /**
     * Update the UI to show the given directory as the user-selected directory
     * @param dir directory selected by the user
     */
    private void setChosenDirectory(File dir) {
        String path = dir.getAbsolutePath();
        filePathLabel.setText(path);
        setSummaryBarTitle(NewScanText.SUMMARY_BAR_TITLE_HEADER_SELECTED, dir.getAbsolutePath(), true, true);
        setSummaryBarSubtitle(NewScanText.SUMMARY_BAR_SUBTITLE_SELECTED);

        updateModel(dir);
        createAndSetNextController();
        enableNextButton();
    }


    /**
     * Update the model with the user selection
     * @param dir directory selected by user
     */
    private void updateModel(File dir) {
        ScanController model = new ScanController(dir);
        app.setModel(model);
    }

    /**
     * Instantiate the controller for the next scene
     */
    private void createAndSetNextController() {
        PrepareToScan pts = new PrepareToScan(app);
        pts.setPrevController(this);
        setNextController(pts);
    }
}
