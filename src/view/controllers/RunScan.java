package view.controllers;

import config.Config;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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
import view.util.dialogues.AppConfirmationDialogue;
import view.util.TaskProgressTracker;
import view.util.dialogues.AppInformationDialogue;

import java.io.IOException;

import static view.util.FormatConverter.milliSecondsToTime;

public class RunScan extends GUIController {                                                                            // TODO: create a parent for this and PrepareToScan

    /* UI copy */
    private String NAV_BAR_TITLE = "Run scan";
    private String MAIN_CONTENT_TITLE_BEFORE_START = "Ready to Scan";
    private String NEXT_BUTTON_TEXT_WITH_RESULTS = "View Results";
    private String NEXT_BUTTON_TEXT_NO_RESULTS = "New Scan";
    private String CANCEL_BUTTON_TEXT_NO_RESULTS = "Exit";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "%d files will be scanned";
    private String SUMMARY_BAR_SUBTITLE_COMPLETE = "Scan complete. Click next to view results.";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Scanning";
    private String STATS_DEFAULT = "Not started";
    private String FILE_COUNT_TEMPLATE = "%d (%6.2f%%)";
    private String CANCELLED_TEXT_ON_BAR = "Cancelling scan...";

    /* UI constants */
    private double PROGRESS_BAR_MIN_VALUE = 0.09;

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
        if (model.getStrategy().getClass().equals(Config.quick.getStrategy())){
            setContentTitle(Config.quick.getUiName());
            // TODO: check for other scan types when implemented
        } else {
            setContentTitle(MAIN_CONTENT_TITLE_BEFORE_START);
        }

        setNextButtonText(NEXT_BUTTON_TEXT_WITH_RESULTS);
        setNavBarTitle(NAV_BAR_TITLE);
        long totalFilesToBeScanned = model.getProgress().getDone();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFilesToBeScanned));

        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
        filePathLabel.setText(getPathToCurrentRootDir());
    }

    @Override
    Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/RunScan.fxml"));                         // TODO: replace with static config reference

            ObservableList<Node> rootChildren = root.getChildren();
            this.filePathLabel = (Label) rootChildren.get(0);

            StackPane stackPane1 = (StackPane) rootChildren.get(1);
            ObservableList<Node> stackPaneChildren1 = stackPane1.getChildren();
            this.startScanButton = (Button) stackPaneChildren1.get(0);
            this.progressBar = (ProgressBar) stackPaneChildren1.get(1);
            this.completeLabel = (Label) stackPaneChildren1.get(2);

            StackPane stackPane2 = (StackPane) rootChildren.get(2);
            ObservableList<Node> stackPaneChildren2 = stackPane2.getChildren();
            this.filesScanned = (Label) stackPaneChildren2.get(1);

            StackPane stackPane3 = (StackPane) rootChildren.get(3);
            ObservableList<Node> stackPaneChildren3 = stackPane3.getChildren();
            this.suspectedDuplicates = (Label) stackPaneChildren3.get(1);

            StackPane stackPane4 = (StackPane) rootChildren.get(4);
            ObservableList<Node> stackPaneChildren4 = stackPane4.getChildren();
            this.etaLabel = (Label) stackPaneChildren4.get(1);

            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
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

    private void OnCancel(ActionEvent e) {                                                                              // TODO: show 'are you sure?' dialogue
        setUIToCancelledMode();
        if (tracker != null) {
            tracker.stop();
        }
        model.stop();                                                                                                   // TODO: catch exception/error handling
        reset();
    }                                                                                                                   // TODO: call this on back button press too

    private void createAndSetNextController() {
        Results r = new Results(app, model.getResults());
        setNextController(r);
    }

    private void setComplete() {
        setCompleteLabelVisible();
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_COMPLETE);
        disableCancelButton();
        createAndSetNextController();
        enableNextButton();

        if (model.getResults().size() == 0) {
            setNextButtonText(NEXT_BUTTON_TEXT_NO_RESULTS);
            setCancelButtonText(CANCEL_BUTTON_TEXT_NO_RESULTS);
            setNextController(new NewScan(app));
            setCancelButtonOnAction(event -> Platform.exit());
            enableCancelButton();
            AppInformationDialogue dialogue = new AppInformationDialogue(
                    "No Duplicates Found",
                    "No duplicates were found during the scan!",
                    "You may choose to perform a new scan or exit the application."
            );
            dialogue.getConfirmation();
        }
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