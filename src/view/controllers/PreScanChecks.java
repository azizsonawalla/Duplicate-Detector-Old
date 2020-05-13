package view.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import model.async.threadPool.AppThreadPool;
import model.searchModel.ScanController;
import model.util.Progress;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.DuplicateDetectorGUIApp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreScanChecks extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Preparing to scan";
    private String MAIN_CONTENT_TITLE = "Pre-scan Analysis";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_SUBTITLE_DEFAULT = "Analyzing folder";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Chosen Folder";
    private String FILE_COUNT_TEMPLATE = "Files found: %d";
    private String CANCELLED_TEXT_ON_BAR = "Cancelling analysis...";

    /* UI controls */
    private Label filePathLabel, completeLabel, fileCountLabel;
    private ProgressBar progressBar;

    private ScanController model;
    private TrackProgress tracker;

    public PreScanChecks(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        model = app.getModel();
        setContent(loadMainContent());
        configureControls();
        initCopy();

        model.startPreSearch();
        tracker = new TrackProgress(model, 1000);
        AppThreadPool.getInstance().submit(tracker);
        setCancelButtonOnAction(this::OnCancel);
    }

    private void configureControls() {
        disableNextButton();
        completeLabel.setVisible(false);
        progressBar.setProgress(-1);
    }

    private void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE_DEFAULT);

        File chosenFolder = model.getRootDirectories().get(0);
        setSummaryBarTitle(SUMMARY_BAR_HEADER_DEFAULT, chosenFolder.getAbsolutePath(), true, true);
        filePathLabel.setText(chosenFolder.getAbsolutePath());
        setFileCount(0);
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

    private Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/PreScanChecks.fxml"));                   // TODO: replace with static config reference

            ObservableList<Node> rootChildren = root.getChildren();
            this.filePathLabel = (Label) rootChildren.get(0);
            this.fileCountLabel = (Label) rootChildren.get(2);

            StackPane stackPane = (StackPane) rootChildren.get(1);
            ObservableList<Node> stackPaneChildren = stackPane.getChildren();
            this.progressBar = (ProgressBar) stackPaneChildren.get(0);
            this.completeLabel = (Label) stackPaneChildren.get(1);

            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("AppError loading content");
    }

    private void OnCancel(ActionEvent e) {                                                                              // TODO: show 'are you sure?' dialogue
        setUIToCancelledMode();
        if (tracker != null) {
            tracker.stop();
        }
        model.stop();                                                                                                   // TODO: catch exception/error handling
        reset();
    }

    private void setNextController() {
        // TODO: next controller not yet created
        throw new NotImplementedException();
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
            setComplete();
        }

        public void stop() {
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

        private void setComplete() {
            setProgressBarLevel(1.0);
            setCompleteLabelVisible();
            setNextController();
            enableNextButton();
            disableCancelButton();
        }
    }
}
