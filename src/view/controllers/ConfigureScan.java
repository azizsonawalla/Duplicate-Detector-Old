package view.controllers;

import config.Config;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import model.searchModel.searchStrategies.ContentsHashStrategy;
import model.searchModel.searchStrategies.MetadataHashStrategy;
import view.DuplicateDetectorGUIApp;
import view.util.FXMLUtils;
import view.util.dialogues.AppErrorDialogue;

import java.util.Arrays;
import java.util.List;

import static view.textBindings.ConfigureScanText.*;

/**
 * UI Controller for the ConfigureScan scene
 */
public class ConfigureScan extends GUIController {

    /* UI controls */
    private GridPane quickScan, fullScan, advScan;

    /**
     * Create an instance of the ConfigureScan controller
     * @param app instance of the JavaFX application associated with this controller
     */
    ConfigureScan(DuplicateDetectorGUIApp app) {
        super(app);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();

        List<GridPane> allStrategyButtons = Arrays.asList(quickScan, fullScan, advScan);
        allStrategyButtons.forEach((GridPane g) -> g.setOnMouseClicked(this::setStrategyAndGoToNextScene));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarHeadWithFilePath(SUMMARY_BAR_HEADER_DEFAULT);

        long totalFileCount = app.tryWithFatalAppError(() -> model.getProgress().getDone(), FAILED_TO_GET_MODEL_DATA_MSG);
        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFileCount));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node loadMainContent() throws Exception {
        GridPane root = FXMLLoader.load(getClass().getResource(Config.LAYOUTS_CONFIGURE_SCAN_FXML));
        quickScan = (GridPane) FXMLUtils.getChildWithId(root, "QuickScanButton");
        fullScan = (GridPane) FXMLUtils.getChildWithId(root, "FullScanButton");
        advScan = (GridPane) FXMLUtils.getChildWithId(root, "AdvancedScanButton");
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cleanupSelf() {
        super.cleanupSelf();
        quickScan = null;
        fullScan = null;
        advScan = null;
    }

    /**
     * Identifies search strategy based on user input, updates model accordingly, and switches to the next scene
     * @param e user input event
     */
    private void setStrategyAndGoToNextScene(MouseEvent e) {
        Object source = e.getSource();
        if (source.equals(quickScan)) {
            model.setStrategy(new MetadataHashStrategy());
        } else if (source.equals(fullScan)) {
            model.setStrategy(new ContentsHashStrategy());
        } else {
            log.error("Couldn't identify scan strategy from user input.");
            AppErrorDialogue.showError(UNKNOWN_STRATEGY_MSG);
        }

        createAndSetNextController();
        goToNextScene();
    }

    /**
     * Creates the controller for the next scene.
     */
    private void createAndSetNextController() {
        RunScan rs = new RunScan(app);
        setNextController(rs);
    }
}
