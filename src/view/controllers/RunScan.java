package view.controllers;

import config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.async.threadPool.AppThreadPool;
import util.Progress;
import view.DuplicateDetectorGUIApp;
import view.util.FXMLUtils;
import view.util.TaskProgressTracker;
import view.util.dialogues.AppConfirmationDialogue;
import view.util.dialogues.AppInformationDialogue;

import java.io.File;
import java.util.List;

import static view.textBindings.RunScanText.*;
import static view.util.FXMLUtils.getChildWithId;
import static view.util.FormatConverter.milliSecondsToTime;

public class RunScan extends GUIController {

    /* UI constants */
    private static final double PROGRESS_BAR_MIN_VALUE = 0.09;

    /* UI controls */
    private Label filePathLabel, completeLabel, filesScanned, suspectedDuplicates, etaLabel;
    private Button startScanButton;
    private ProgressBar progressBar;
    private TaskProgressTracker tracker;

    RunScan(DuplicateDetectorGUIApp app) {
        super(app);
    }

    @Override
    void configureControls() {
        disableNextButton();
        disableCancelButton();

        completeLabel.setVisible(false);
        progressBar.setProgress(0);
        progressBar.setVisible(false);

        filesScanned.setText(STATS_DEFAULT);
        suspectedDuplicates.setText(STATS_DEFAULT);
        etaLabel.setText(STATS_DEFAULT);

        setCancelButtonOnAction(this::OnCancel);
        startScanButton.setOnAction(this::startScan);
    }

    @Override
    void initCopy() {
        Class strategyClass = model.getStrategy().getClass();
        if (strategyClass.equals(Config.quick.getStrategy())){
            setContentTitle(Config.quick.getUiName());
        } else if (strategyClass.equals(Config.full.getStrategy())) {
            setContentTitle(Config.full.getUiName());
        } else {
            setContentTitle(MAIN_CONTENT_TITLE_BEFORE_START);
        }

        setNextButtonText(NEXT_BUTTON_TEXT_WITH_RESULTS);
        setNavBarTitle(NAV_BAR_TITLE);
        long totalFilesToBeScanned = app.tryWithFatalAppError(() -> model.getProgress().getDone(), GET_DONE_ERROR_MSG);
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFilesToBeScanned));

        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
        filePathLabel.setText(getPathToCurrentRootDir());
    }

    @Override
    protected Node loadMainContent() throws Exception {

        GridPane root = FXMLUtils.fxmlLoaderFromFile(Config.LAYOUTS_RUN_SCAN_FXML).load();
        this.filePathLabel = (Label) getChildWithId(root, "filePathLabel");

        StackPane progressStackPane = (StackPane) getChildWithId(root, "progressStackPane");
        StackPane statsStackPane1 = (StackPane) getChildWithId(root, "statsStackPane1");
        StackPane statsStackPane2 = (StackPane) getChildWithId(root, "statsStackPane2");
        StackPane statsStackPane3 = (StackPane) getChildWithId(root, "statsStackPane3");

        this.startScanButton = (Button) getChildWithId(progressStackPane, "startScanButton");
        this.progressBar = (ProgressBar) getChildWithId(progressStackPane, "progressBar");
        this.completeLabel = (Label) getChildWithId(progressStackPane, "completeLabel");

        this.filesScanned = (Label) getChildWithId(statsStackPane1, "filesScanned");

        this.suspectedDuplicates = (Label) getChildWithId(statsStackPane2, "suspectedDuplicates");

        this.etaLabel = (Label) getChildWithId(statsStackPane3, "eta");

        return root;
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }

    private void startScan(ActionEvent e) {
        createAndStartTracker();
        try {
            model.startSearch();
        } catch (Exception e2) {
            e2.printStackTrace();
            // TODO: error handling
        }
        startScanButton.setVisible(false);
        progressBar.setVisible(true);
        enableCancelButton();
        suspectedDuplicates.setVisible(true);
        etaLabel.setVisible(true);
    }

    private void createAndStartTracker() {
        tracker = new TaskProgressTracker(500, 2000, 200, Long.MAX_VALUE, model::isSearchInProgress,model::isSearchDone,
                                            this::getAndSetProgressStats, this::setComplete);                           // TODO: move to config
        AppThreadPool.getInstance().submit(tracker);
    }

    private void setProgressStats(long scanned, long duplicates, long eta, double percentageDone) {                     // TODO: javadoc
        if (scanned > -1) {
            filesScanned.setText(String.format(FILE_COUNT_TEMPLATE, scanned, percentageDone*100));
        }
        if (duplicates > -1) {
            suspectedDuplicates.setText(Long.toString(duplicates));
        }
        if (eta > -1) {
            etaLabel.setText(milliSecondsToTime(eta));
        }
        if (percentageDone > -1) {
            setProgressBarLevel(Math.max(percentageDone, PROGRESS_BAR_MIN_VALUE));                                      // min value helps give illusion of progress
        }
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
        completeLabel.setText(CANCELLED_TEXT_ON_BAR);
        setCompleteLabelVisible();
    }

    private void OnCancel(ActionEvent e) {
        AppConfirmationDialogue dialogue = new AppConfirmationDialogue(
                RUNSCAN_STOP_SCAN_TITLE,
                RUNSCAN_STOP_SCAN_HEADER,
                RUNSCAN_STOP_SCAN_MSG
        );
        if (!dialogue.getConfirmation()) {
            return;
        }

        setUIToCancelledMode();
        if (tracker != null) {
            tracker.stop();
        }
        app.tryWithFatalAppError(() -> model.stop(), FAILED_TO_STOP_THE_CURRENT_SCAN);
        reset();
    }                                                                                                                   // TODO: call this on back button press too

    private void createAndSetNextController() {
        List<List<File>> results = app.tryWithFatalAppError(() -> model.getResults(), FAILED_TO_PROCESS_SCAN_RESULTS);
        Results r = new Results(app, results);
        setNextController(r);
    }

    private void setComplete() {
        setCompleteLabelVisible();
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_COMPLETE);
        disableCancelButton();
        createAndSetNextController();
        enableNextButton();

        List<List<File>> results = app.tryWithFatalAppError(() -> model.getResults(), FAILED_TO_PROCESS_SCAN_RESULTS);
        if (results.size() == 0) {
            setUIToNoResultsMode();
        }
    }

    private void setUIToNoResultsMode() {
        setNextButtonText(NEXT_BUTTON_TEXT_NO_RESULTS);
        setNextController(new NewScan(app));
        enableNextButton();
        swapCancelButtonForExitButton();
        enableCancelButton();
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_NO_RESULTS);
        AppInformationDialogue dialogue = new AppInformationDialogue(
                NO_DUPLICATES_FOUND_TITLE,
                NO_DUPLICATES_FOUND_HEADER,
                NO_DUPLICATES_FOUND_MSG
        );
        dialogue.getConfirmation();
    }

    private void getAndSetProgressStats() {
        Progress progress;
        try {
            progress = model.getProgress();
        } catch (Exception e) {
            return;
            // TODO: log
        }

        long scanned = progress.getDone();
        long duplicates = progress.getPositives();
        long eta = progress.getEta();
        long remaining = progress.getRemaining();
        double percentageDone = scanned*1. / (scanned+remaining);
        setProgressStats(scanned, duplicates, eta, percentageDone);
    }
}