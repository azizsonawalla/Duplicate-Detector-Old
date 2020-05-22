package view.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import static view.util.FXMLUtils.getChildWithId;

public class Results extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Scan results";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Results for";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "Found %d duplicate sets";
    private String LOAD_BUTTON_TEXT = "Load more results";
    private String SELECTED_COUNT_TEMPLATE = "%d images selected";

    /* Action items for the Bulk Action menu */
    private enum Action {
        DELETE("Delete");

        String label;

        Action(String label)  { this.label = label; }

        static List<String> getLabels() {
            List<String> labels = new LinkedList<>();
            for (Action a: Action.values()) {
                labels.add(a.label);
            }
            return labels;
        }

        static Action getActionFromLabel(String label) {
            for (Action a: Action.values()) {
                if (a.label.equals(label)) {
                    return a;
                }
            }
            return null;
        }
    }

    /* UI controls */
    private GridPane resultsPane;
    private Button loadMoreButton, actionApplyButton, clearSelectionButton;
    private Label selectedCountLabel;
    private List<RenderedResult> renderedResults;
    private long selectedCount = 0;
    private ComboBox<String> actionMenu;

    /* Model data */
    private List<List<File>> results;

    /* Other Constants */
    private int RESULT_GROUP_SIZE = 10;

    Results(DuplicateDetectorGUIApp app, List<List<File>> results) {
        super(app);
        renderedResults = new LinkedList<>();
        this.results = results;
        resultsPane = createResultsPane(results);
    }

    @Override
    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();
        loadMoreButton.setOnAction(event -> loadNextSetOfResults());
        actionApplyButton.setDisable(true);
        clearSelectionButton.setOnAction(this::onClearSelection);
        actionMenu.setOnAction(this::onActionSelection);
    }

    @Override
    void initCopy() {
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
        loadMoreButton.setText(LOAD_BUTTON_TEXT);
        updateSelectedCountLabelValue(0);

        long duplicateCount = model.getProgress().getPositives();
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, duplicateCount));

        actionMenu.getItems().addAll(Action.getLabels());
    }

    @Override
    Node loadMainWindow() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/Results.fxml"));                         // TODO: replace with static config reference
            ObservableList<Node> rootChildren = root.getChildren();

            selectedCountLabel = (Label) getChildWithId(root, "selectedCount");
            actionMenu = (ComboBox<String>) getChildWithId(root, "actionMenu");
            actionApplyButton = (Button) getChildWithId(root, "actionApplyButton");
            clearSelectionButton = (Button) getChildWithId(root, "clearSelectionButton");

            ScrollPane s = (ScrollPane) rootChildren.get(2);                                                            // TODO: replace all FXML child access from index to id
            GridPane g = (GridPane) s.getContent();
            loadMoreButton = (Button) g.getChildren().get(0);
            g.getChildren().add(resultsPane);

            loadNextSetOfResults();

            return root;
        } catch (Exception e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void loadNextSetOfResults() {
        int startIdx = renderedResults.size();
        int endIdx = Math.min(startIdx + RESULT_GROUP_SIZE-1, results.size()-1);

        List<RenderedResult> newRenderedResults = addResultsToResultsPane(results, resultsPane, startIdx, endIdx);
        loadImagePreviews(newRenderedResults);

        for (RenderedResult res: newRenderedResults) {
            res.getCheckBox().setOnAction(this::onCheckBoxToggle);
        }

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

        GridPane.setRowIndex(resultsPane, 0);
        GridPane.setColumnIndex(resultsPane, 1);
        return resultsPane;
    }

    private void loadImagePreviews(List<RenderedResult> newRenderedResults) {
        log.debug("Creating preview loading threads");
        for (RenderedResult rr: newRenderedResults) {
            AppThreadPool.getInstance().submit(new ImagePreviewLoader(rr.getFile(), rr.getPreviewPane()));
        }
        log.debug("Done creating preview loading threads");
    }

    private void updateSelectedCountLabelValue(long value) {
        selectedCountLabel.setText(String.format(SELECTED_COUNT_TEMPLATE, value));
    }

    private void onCheckBoxToggle(ActionEvent event) {
        CheckBox source = (CheckBox) event.getSource();
        if (source.isSelected()) {
            selectedCount++;
        } else {
            selectedCount--;
        }
        updateSelectedCountLabelValue(selectedCount);
    }

    private void onClearSelection(ActionEvent event) {
        for (RenderedResult res: renderedResults) {
            res.getCheckBox().setSelected(false);
        }
        this.selectedCount = 0;
        updateSelectedCountLabelValue(selectedCount);
    }

    private void onActionSelection(ActionEvent event) {
        String selected = actionMenu.getSelectionModel().getSelectedItem();
        actionApplyButton.setDisable(Action.getActionFromLabel(selected) == null);
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }
}
