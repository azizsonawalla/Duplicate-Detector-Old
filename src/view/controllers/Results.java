package view.controllers;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import model.async.threadPool.AppThreadPool;
import view.DuplicateDetectorGUIApp;
import view.controllers.helpers.ImagePreviewLoader;
import view.controllers.helpers.RenderedResult;
import view.controllers.helpers.ResultsRenderer;
import view.util.AppConfirmationDialogue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static util.FileSystemUtil.deleteFiles;
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
    private String CONFIRMATION_DIALOG_TITLE_TEMPLATE = "Confirm %s";
    private String CONFIRMATION_DIALOG_HEADER_TEMPLATE = "Are you sure you want to %s %d file(s)?";
    private String CONFIRMATION_DIALOG_MSG = "Click Ok to continue or Cancel to go back.";


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
    private List<List<RenderedResult>> renderedResults;
    private long selectedCount = 0;
    private ComboBox<String> actionMenu;

    /* Model data */
    private List<List<File>> results;

    /* Other Constants */
    private int RESULT_SETS_PER_LOAD = 10;

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
        actionApplyButton.setOnAction(this::onActionApply);
    }

    @Override
    void initCopy() {
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);
        loadMoreButton.setText(LOAD_BUTTON_TEXT);
        updateSelectedCountLabel();

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
        int endIdx = Math.min(startIdx + RESULT_SETS_PER_LOAD -1, results.size()-1);

        List<List<RenderedResult>> newRenderedResults = addResultsToResultsPane(results, resultsPane, startIdx, endIdx);
        loadImagePreviews(newRenderedResults);

        for (List<RenderedResult> resultSet: newRenderedResults) {
            for (RenderedResult res: resultSet) {
                res.getCheckBox().setOnAction(this::onCheckBoxToggle);
            }
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

    private void loadImagePreviews(List<List<RenderedResult>> newRenderedResults) {
        for (List<RenderedResult> resultSet: newRenderedResults) {
            for (RenderedResult rr: resultSet) {
                AppThreadPool.getInstance().submit(new ImagePreviewLoader(rr.getFile(), rr.getPreviewPane()));
            }
        }
    }

    private void updateSelectedCountLabel() {
        selectedCountLabel.setText(String.format(SELECTED_COUNT_TEMPLATE, selectedCount));
    }

    private void onCheckBoxToggle(ActionEvent event) {
        CheckBox source = (CheckBox) event.getSource();
        if (source.isSelected()) {
            selectedCount++;
        } else {
            selectedCount--;
        }
        updateSelectedCountLabel();
    }

    private void onClearSelection(ActionEvent event) {
        for (List<RenderedResult> resultSet: renderedResults) {
            for (RenderedResult res: resultSet) {
                res.getCheckBox().setSelected(false);
            }
        }
        this.selectedCount = 0;
        updateSelectedCountLabel();
    }

    private void onActionSelection(ActionEvent event) {
        String selected = actionMenu.getSelectionModel().getSelectedItem();
        actionApplyButton.setDisable(Action.getActionFromLabel(selected) == null);
    }

    private void onActionApply(ActionEvent event) {

        if (selectedCount == 0) {
            log.debug("No results selected");
            return;
        }

        String selectedAction = actionMenu.getSelectionModel().getSelectedItem();
        Action action = Action.getActionFromLabel(selectedAction);
        if (action == null) {
            log.error("No action selected");
            // TODO: error handling
        }

        if (!actionConfirmed(action)) {
            return;
        }

        List<RenderedResult> selectedResults = getSelectedResults();
        List<File> selectedFiles = extractFiles(selectedResults);

        switch (action) {
            case DELETE:
                try {
                    deleteFiles(selectedFiles);
                } catch (IOException e) {
                    // TODO: error handling
                }
                break;
            default:
                log.error("Couldn't identify selected action: " + action.toString());
                // TODO: error handling
        }

        onClearSelection(null);
        ResultsRenderer.disableResultPanes(selectedResults);
    }

    private boolean actionConfirmed(Action a) {
        AppConfirmationDialogue dialogue = new AppConfirmationDialogue (
            String.format(CONFIRMATION_DIALOG_TITLE_TEMPLATE, a.label),
            String.format(CONFIRMATION_DIALOG_HEADER_TEMPLATE, a.label.toLowerCase(), selectedCount),
            CONFIRMATION_DIALOG_MSG
        );
        return dialogue.getConfirmation();
    }

    private List<RenderedResult> getSelectedResults() {
        List<RenderedResult> selectedResults = new LinkedList<>();
        for (List<RenderedResult> resultSet: renderedResults) {
            for (RenderedResult res: resultSet) {
                if (res.getCheckBox().isSelected()) {
                    selectedResults.add(res);
                }
            }
        }
        return selectedResults;
    }

    private List<File> extractFiles(List<RenderedResult> results) {
        List<File> selectedFiles = new LinkedList<>();
        for (RenderedResult res: results) {
            selectedFiles.add(res.getFile());
        }
        return selectedFiles;
    }

    @Override
    protected void cleanupSelf() {
        // TODO:
    }
}
