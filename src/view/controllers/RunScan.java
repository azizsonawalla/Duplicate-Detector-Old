package view.controllers;

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
import model.searchModel.ScanController;
import model.util.Progress;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.DuplicateDetectorGUIApp;

import java.io.IOException;

public class RunScan extends GUIController {

    /**
     * To Fix:
     * - View Results button text doesn't fit
     **/

    /* UI copy */
    private String NAV_BAR_TITLE = "Run scan";
    private String MAIN_CONTENT_TITLE_BEFORE_START = "Ready to Scan";
    private String NEXT_BUTTON_TEXT = "View Results";
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
    private TrackProgress tracker;

    RunScan(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

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

    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE_BEFORE_START);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, model.getProgress().getDone()));

        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
        filePathLabel.setText(getPathToCurrentRootDir());
    }

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

    private void startScan(ActionEvent e) {
        tracker = new TrackProgress(model, 500);
        AppThreadPool.getInstance().submit(tracker);
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

    private void setProgressStats(long scanned, long duplicates, long eta, double percentageDone) {                     // TODO: javadoc
        Platform.runLater(() -> {
            filesScanned.setText(String.format(FILE_COUNT_TEMPLATE, scanned, percentageDone*100));
            suspectedDuplicates.setText(Long.toString(duplicates));
            etaLabel.setText(milliSecondsToTime(eta));
            progressBar.setProgress(Math.max(percentageDone, PROGRESS_BAR_MIN_VALUE));                                  // min value helps give illusion of progress
        });
    }

    private String milliSecondsToTime(long milli) {
        long seconds = milli/1000;
        long mins = seconds/60;
        long hours = mins/60;
        long days = hours/24;

        seconds -= mins*60;
        mins -= hours*60;
        hours -= days*24;

        if (days > 0) {
            return String.format("%dd %dh %dm %ds", days, hours, mins, seconds);
        }
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, mins, seconds);
        }
        if (mins > 0) {
            return String.format("%dm %ds", mins, seconds);
        }
        return String.format("%ds", seconds);
    }

    private void setProgressBarLevel(double p) {
        Platform.runLater(() -> progressBar.setProgress(p));                                                            // TODO: remove use of runLater // TODO: javadoc
    }

    private void setCompleteLabelVisible() {
        Platform.runLater(() -> completeLabel.setVisible(true));                                                        // TODO: remove use of runLater // TODO: javadoc
    }

    private void setUIToCancelledMode() {
        progressBar.setProgress(1.0);
        progressBar.getStyleClass().add("cancelled-progress-bar");
        completeLabel.setText(CANCELLED_TEXT_ON_BAR);
        completeLabel.setVisible(true);
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
        // TODO:
        throw new NotImplementedException();
    }

    private void setComplete() {
        setProgressBarLevel(1.0);
        setCompleteLabelVisible();
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_COMPLETE);
        disableCancelButton();
//        createAndSetNextController();
        enableNextButton();
    }

    private class TrackProgress implements Runnable {                                                                   // TODO: javadoc

        private final ScanController model;
        private final long interval;
        private boolean interrupted = false;

        TrackProgress(ScanController model, long interval) {
            this.model = model;
            this.interval = interval;
        }

        @Override
        public void run() {

            // If search hasn't started, wait for a bit
            while (!model.isSearchInProgress()) {                                                                       // TODO: add timeout
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();                                                                                // TODO: error handling
                }
            }

            while (!model.isSearchDone()) {
                if (interrupted) return;
                getAndSetProgressStats();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();                                                                                // TODO: error handling
                }
            }

            if (interrupted) return;
            getAndSetProgressStats();
            if (interrupted) return;
            Platform.runLater(RunScan.this::setComplete);                                                               // TODO: remove use of runlater
        }

        void stop() {
            interrupted = true;
        }

        private void getAndSetProgressStats() {
            try {
                Progress progress = model.getProgress();
                long scanned = progress.getDone();
                long duplicates = progress.getPositives();
                long eta = progress.getEta();
                long remaining = progress.getRemaining();
                double percentageDone = scanned*1. / (scanned+remaining);
                setProgressStats(scanned, duplicates, eta, percentageDone);
            } catch (Exception e) {
                // TODO: log
            }
        }
    }
}