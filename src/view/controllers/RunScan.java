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
import model.searchModel.ScanController;
import model.util.Progress;
import view.DuplicateDetectorGUIApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RunScan extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Run scan";
    private String MAIN_CONTENT_TITLE_BEFORE_START = "Ready to Scan";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "%d files will be scanned";
    private String SUMMARY_BAR_SUBTITLE_COMPLETE = "Analyses complete. Click next to configure the scan.";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Scanning";
    private String FILE_COUNT_DEFAULT = "Scan hasn't started";
    private String FILE_COUNT_TEMPLATE = "Files found: %d";
    private String CANCELLED_TEXT_ON_BAR = "Cancelling analysis...";

    /* UI controls */
    private Label filePathLabel, completeLabel, fileCountLabel;
    private Button startScanButton;
    private ProgressBar progressBar;
    private TrackProgress tracker;

    public RunScan(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    void configureControls() {
        disableNextButton();
        disableCancelButton();
        completeLabel.setVisible(false);
        progressBar.setProgress(0);
        setCancelButtonOnAction(this::OnCancel);
        progressBar.setVisible(false);
        fileCountLabel.setText(FILE_COUNT_DEFAULT);
    }

    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE_BEFORE_START);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
//        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_DEFAULT);
//
//        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
//        filePathLabel.setText(getPathToCurrentRootDir());
//        setFileCount(0);
    }

    Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/RunScan.fxml"));                         // TODO: replace with static config reference

            ObservableList<Node> rootChildren = root.getChildren();
            this.filePathLabel = (Label) rootChildren.get(0);
            this.fileCountLabel = (Label) rootChildren.get(2);

            StackPane stackPane = (StackPane) rootChildren.get(1);
            ObservableList<Node> stackPaneChildren = stackPane.getChildren();
            this.startScanButton = (Button) stackPaneChildren.get(0);
            this.progressBar = (ProgressBar) stackPaneChildren.get(1);
            this.completeLabel = (Label) stackPaneChildren.get(2);

            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void setFileCount(int i) {
        Platform.runLater(() -> fileCountLabel.setText(String.format(FILE_COUNT_TEMPLATE, i)));                         // TODO: remove use of runLater // TODO: javadoc
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
        ConfigureScan c = new ConfigureScan(app, this);
        setNextController(c);
    }

    private void setComplete() {
        setProgressBarLevel(1.0);
        setCompleteLabelVisible();
        createAndSetNextController();
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_COMPLETE);
        enableNextButton();
        disableCancelButton();
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
            while (!model.isPreSearchDone()) {
                if (interrupted) return;
                updateFileCount();
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();                                                                                // TODO: error handling
                }
            }
            if (interrupted) return;
            updateFileCount();
            if (interrupted) return;
            Platform.runLater(RunScan.this::setComplete);                                                       // TODO: remove use of runlater
        }

        void stop() {
            interrupted = true;
        }

        private void updateFileCount() {
            try {
                Progress progress = model.getProgress();
                int count = progress.getDone();
                setFileCount(count);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
