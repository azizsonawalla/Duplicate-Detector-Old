package view.controllers;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.async.threadPool.AppThreadPool;
import util.Progress;
import view.DuplicateDetectorGUIApp;
import view.textBindings.PrepareToScanText;
import view.util.TaskProgressTracker;
import view.util.dialogues.AppConfirmationDialogue;

import java.net.URL;
import java.util.ResourceBundle;

import static view.util.FXMLUtils.getChildWithId;

/**
 * UI Controller for the PrepareToScan scene
 */
public class PrepareToScan extends GUIController {

    private Label filePathLabel, progressBarOverlayLabel, fileCountLabel;
    private ProgressBar progressBar;
    private TaskProgressTracker tracker;

    /**
     * Create an instance of the PrepareToScan controller
     * @param app instance of the JavaFX application associated with this controller
     */
    PrepareToScan(DuplicateDetectorGUIApp app) {
        super(app);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        startPreSearch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void configureControls() {
        disableNextButton();
        progressBarOverlayLabel.setVisible(false);
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        setCancelButtonOnAction(this::OnCancel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void initCopy() {
        setContentTitle(PrepareToScanText.MAIN_CONTENT_TITLE);
        setNextButtonText(PrepareToScanText.NEXT_BUTTON_TEXT);
        setNavBarTitle(PrepareToScanText.NAV_BAR_TITLE);
        setSummaryBarSubtitle(PrepareToScanText.SUMMARY_BAR_SUBTITLE_DEFAULT);

        setSummaryBarHeadWithFilePath(PrepareToScanText.SUMMARY_BAR_HEADER_DEFAULT);
        filePathLabel.setText(getPathToCurrentRootDir());
        setFileCount(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Node loadMainContent() throws Exception {
        GridPane root = FXMLLoader.load(getClass().getResource(Config.LAYOUTS_PREPARE_TO_SCAN_FXML));

        this.filePathLabel = (Label) getChildWithId(root, "filePathLabel");
        this.fileCountLabel = (Label) getChildWithId(root, "fileCountLabel");

        StackPane stackPane = (StackPane) getChildWithId(root, "progressStackPane");
        this.progressBar = (ProgressBar) getChildWithId(stackPane, "progressBar");
        this.progressBarOverlayLabel = (Label) getChildWithId(stackPane, "progressBarOverlayLabel");

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cleanupSelf() {
        super.cleanupSelf();
        filePathLabel = null;
        progressBarOverlayLabel = null;
        fileCountLabel = null;
        progressBar = null;
        tracker.stop();
        tracker = null;
    }

    /**
     * Start the pre-search stage in the model and sync the progress with the UI
     */
    private void startPreSearch() {
        app.tryWithFatalAppError(() -> model.startPreSearch(), PrepareToScanText.PRE_SCAN_START_ERROR_MSG);
        createAndStartTracker();
    }

    /**
     * Create tracker object for pre-search to update the UI
     */
    private void createAndStartTracker() {
        tracker = new TaskProgressTracker (
                Config.PRE_SCAN_WAIT_POLL_INTERVAL_MS,
                Config.PRE_SCAN_WAIT_TIMEOUT_MS,
                Config.PRE_SCAN_POLL_INTERVAL_MS,
                Config.PRE_SCAN_TIMEOUT_MS,
                model::isPreSearchInProgress,
                model::isPreSearchDone,
                this::getAndSetProgressStats,
                this::setComplete
        );
        AppThreadPool.getInstance().submit(tracker);
    }

    /**
     * Set the value for the file count label
     * @param fileCount new file count
     */
    private void setFileCount(long fileCount) {
        fileCountLabel.setText(String.format(PrepareToScanText.FILE_COUNT_TEMPLATE, fileCount));
    }

    /**
     * Set the progress bar level
     * @param p new progress bar value
     */
    private void setProgressBarLevel(double p) {
        progressBar.setProgress(p);
    }

    /**
     * Make the progress bar overlay label visible to the user
     */
    private void setProgressBarOverlayLabelVisible() {
        progressBarOverlayLabel.setVisible(true);
    }

    /**
     * Updates the UI to reflect a cancelled operation
     */
    private void setUIToCancelledMode() {
        setProgressBarLevel(1.0);
        progressBar.getStyleClass().add("cancelled-progress-bar");
        progressBarOverlayLabel.setText(PrepareToScanText.CANCELLED_TEXT_ON_BAR);
        setProgressBarOverlayLabelVisible();
    }

    /**
     * Cancel the pre-search stage
     * @param e user input action event to trigger cancel
     */
    private void OnCancel(ActionEvent e) {
        AppConfirmationDialogue dialogue = new AppConfirmationDialogue(
                PrepareToScanText.STOP_PRE_SCAN_CONF_TITLE,
                PrepareToScanText.STOP_PRE_SCAN_CONF_HEADER,
                PrepareToScanText.STOP_PRE_SCAN_CONF_MSG
        );
        if (!dialogue.getConfirmation()) {
            return;
        }

        setUIToCancelledMode();
        if (tracker != null) {
            tracker.stop();
        }
        app.tryWithFatalAppError(() -> model.stop(), PrepareToScanText.FAILED_TO_STOP_PRE_SCAN_MSG);
        reset();
    }

    /**
     * Instantiate the controller for the next scene
     */
    private void createAndSetNextController() {
        ConfigureScan c = new ConfigureScan(app);
        setNextController(c);
    }

    private void getAndSetProgressStats() {
        Progress progress = model.getProgress();
        long count = progress.getDone();
        setFileCount(count);
    }

    private void setComplete() {
        setProgressBarLevel(1.0);
        setProgressBarOverlayLabelVisible();
        createAndSetNextController();
        setSummaryBarSubtitle(PrepareToScanText.SUMMARY_BAR_SUBTITLE_COMPLETE);
        enableNextButton();
        disableCancelButton();
    }
}
