package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.async.threadPool.AppThreadPool;
import view.DuplicateDetectorGUIApp;
import view.controllers.helpers.ImagePreviewLoader;
import view.controllers.helpers.RenderedResult;

import java.io.File;
import java.util.*;

import static view.controllers.helpers.ResultsRenderer.addResultsToResultsPane;

public class Results extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Scan results";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Results for";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "Found %d duplicate sets";
    private String LOAD_BUTTON_TEXT = "Load more results";                                                              // TODO: use this

    /* UI controls */
    private GridPane resultsPane;
    private Button loadMoreButton;
    private List<RenderedResult> renderedResults;

    /* Other Constants */
    private int RESULT_GROUP_SIZE = 10;

    Results(DuplicateDetectorGUIApp app) {
        super(app);
        renderedResults = new LinkedList<>();
    }

    @Override
    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();
        loadMoreButton.setOnAction(event -> loadNextSetOfResults());
    }

    @Override
    void initCopy() {
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);

        long duplicateCount = model.getProgress().getPositives();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, duplicateCount));
    }

    @Override
    Node loadMainWindow() {
        try {
            resultsPane = createResultsPane(model.getResults());

            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/Results.fxml"));                         // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            ScrollPane s = (ScrollPane) rootChildren.get(1);                                                            // TODO: replace all FXML child access from index to id
            GridPane g = (GridPane) s.getContent();
            loadMoreButton = (Button) g.getChildren().get(0);

            GridPane.setRowIndex(resultsPane, 0);
            GridPane.setColumnIndex(resultsPane, 1);
            g.getChildren().add(resultsPane);

            loadNextSetOfResults();

            return root;
        } catch (Exception e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void loadNextSetOfResults() {
        List<List<File>> results = model.getResults();
        int startIdx = renderedResults.size();
        int endIdx = Math.min(startIdx + RESULT_GROUP_SIZE-1, results.size()-1);

        List<RenderedResult> newRenderedResults = addResultsToResultsPane(results, resultsPane, startIdx, endIdx);
        loadImagePreviews(newRenderedResults);
        renderedResults.addAll(newRenderedResults);

        if (renderedResults.size() < results.size()) {
            GridPane.setRowIndex(loadMoreButton, renderedResults.size());
        } else {
            loadMoreButton.setVisible(false);                                                                                 // TODO: show end of results message
        }
    }

    private GridPane createResultsPane(List<List<File>> results) {
        GridPane resultsPane = new GridPane();

        int maxSetSize = 0;
        for (List<File> set: results) {
            maxSetSize = Math.max(maxSetSize, set.size());
        }

        resultsPane.getColumnConstraints().add(new ColumnConstraints(100,100,100));
        for (int i = 0; i < maxSetSize; i++) {
            resultsPane.getColumnConstraints().add(new ColumnConstraints(300,300,300));
        }
        return resultsPane;
    }

    private void loadImagePreviews(List<RenderedResult> newRenderedResults) {
        log.debug("Creating preview loading threads");
        for (RenderedResult rr: newRenderedResults) {
            AppThreadPool.getInstance().submit(new ImagePreviewLoader(rr.getFile(), rr.getPreviewPane()));
        }
        log.debug("Done creating preview loading threads");
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }
}
