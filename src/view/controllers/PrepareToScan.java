package view.controllers;

import config.Config;
import javafx.collections.ObservableList;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrepareToScan extends GUIController {

    private Label filePathLabel, completeLabel, fileCountLabel;
    private ProgressBar progressBar;
    private TaskProgressTracker tracker;

    PrepareToScan(DuplicateDetectorGUIApp app) {
        super(app);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        startPreSearch();
    }

    @Override
    void configureControls() {
        disableNextButton();
        completeLabel.setVisible(false);
        progressBar.setProgress(-1);
        setCancelButtonOnAction(this::OnCancel);
    }

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

    @Override
    Node loadMainContent() throws Exception {
        GridPane root = FXMLLoader.load(getClass().getResource(Config.LAYOUTS_PREPARE_TO_SCAN_FXML));

        ObservableList<Node> rootChildren = root.getChildren();
        this.filePathLabel = (Label) rootChildren.get(0);
        this.fileCountLabel = (Label) rootChildren.get(2);

        StackPane stackPane = (StackPane) rootChildren.get(1);
        ObservableList<Node> stackPaneChildren = stackPane.getChildren();
        this.progressBar = (ProgressBar) stackPaneChildren.get(0);
        this.completeLabel = (Label) stackPaneChildren.get(1);

        return root;
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }

    private void startPreSearch() {
        try {
            model.startPreSearch();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: error handling
        }
        createAndStartTracker();
    }

    private void createAndStartTracker() {
        tracker = new TaskProgressTracker(100, 2000, 100, Long.MAX_VALUE, model::isPreSearchInProgress,
                model::isPreSearchDone, this::getAndSetProgressStats, this::setComplete);                               // TODO: move to config
        AppThreadPool.getInstance().submit(tracker);
    }

    private void setFileCount(long i) {
        fileCountLabel.setText(String.format(PrepareToScanText.FILE_COUNT_TEMPLATE, i));                                                  // TODO: javadoc
    }

    private void setProgressBarLevel(double p) {
        progressBar.setProgress(p);                                                                                     // TODO: remove use of runLater // TODO: javadoc
    }

    private void setCompleteLabelVisible() {
        completeLabel.setVisible(true);                                                                                 // TODO: javadoc
    }

    private void setUIToCancelledMode() {
        setProgressBarLevel(1.0);
        progressBar.getStyleClass().add("cancelled-progress-bar");
        completeLabel.setText(PrepareToScanText.CANCELLED_TEXT_ON_BAR);
        setCompleteLabelVisible();
    }

    private void OnCancel(ActionEvent e) {                                                                              // TODO: show 'are you sure?' dialogue
        setUIToCancelledMode();
        if (tracker != null) {
            tracker.stop();
        }
        model.stop();                                                                                                   // TODO: catch exception/error handling
        reset();
    }

    private void createAndSetNextController() {
        ConfigureScan c = new ConfigureScan(app);
        setNextController(c);
    }

    private void getAndSetProgressStats() {
        try {
            Progress progress = model.getProgress();
            long count = progress.getDone();
            setFileCount(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setComplete() {
        setProgressBarLevel(1.0);
        setCompleteLabelVisible();
        createAndSetNextController();
        setSummaryBarSubtitle(PrepareToScanText.SUMMARY_BAR_SUBTITLE_COMPLETE);
        enableNextButton();
        disableCancelButton();
    }
}
