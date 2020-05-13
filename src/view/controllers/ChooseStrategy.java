package view.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import view.DuplicateDetectorGUIApp;

import java.io.IOException;

public class ChooseStrategy extends GUIController {

    /* UI copy */
    private String NAV_BAR_TITLE = "Configure scan";
    private String MAIN_CONTENT_TITLE = "Choose a scan type";
    private String NEXT_BUTTON_TEXT = "Next";
    private String SUMMARY_BAR_HEADER_DEFAULT = "Scanning";
    private String SUMMARY_BAR_SUBTITLE_TEMPLATE = "%d files will be inspected";

    /* UI controls */

    public ChooseStrategy(DuplicateDetectorGUIApp app, GUIController prevController) {
        super(app, prevController);
    }

    void configureControls() {
        hideNextButton();
        hideCancelButton();
        removeMainWindowLogo();
    }

    void initCopy() {
        setContentTitle(MAIN_CONTENT_TITLE);
        setNextButtonText(NEXT_BUTTON_TEXT);
        setNavBarTitle(NAV_BAR_TITLE);

//        long totalFileCount = model.getProgress().getDone();
//        setSummaryBarSubtitle(String.format(SUMMARY_BAR_SUBTITLE_TEMPLATE, totalFileCount));
//
//        File chosenFolder = model.getRootDirectories().get(0);
//        setSummaryBarTitle(SUMMARY_BAR_HEADER_DEFAULT, chosenFolder.getAbsolutePath(), true, true);                     // TODO: move to parent
    }

    Node loadMainContent() {
        try {
            GridPane root = FXMLLoader.load(getClass().getResource("../layouts/ChooseStrategy.fxml"));                  // TODO: replace with static config reference

//            ObservableList<Node> rootChildren = root.getChildren();
//            this.filePathLabel = (Label) rootChildren.get(0);
//            this.fileCountLabel = (Label) rootChildren.get(2);
//
//            StackPane stackPane = (StackPane) rootChildren.get(1);
//            ObservableList<Node> stackPaneChildren = stackPane.getChildren();
//            this.progressBar = (ProgressBar) stackPaneChildren.get(0);
//            this.completeLabel = (Label) stackPaneChildren.get(1);

            return root;
        } catch (IOException e) {
            e.printStackTrace();                                                                                        // TODO: error handling
        }
        return new Label("Error loading content");
    }

    private void createAndSetNextController() {
        ChooseStrategy c = new ChooseStrategy(app, this);
        setNextController(c);
    }
}
