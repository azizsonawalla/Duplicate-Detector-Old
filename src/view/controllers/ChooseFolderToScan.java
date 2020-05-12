package view.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseFolderToScan extends ParentController {

    private String NAV_BAR_TITLE = "Start a new scan";
    private String SUMMARY_BAR_TITLE_HEADER = "No folder selected";
    private String SUMMARY_BAR_TITLE_PREVIEW = "";
    private String SUMMARY_BAR_SUBTITLE = "0 files found";
    private String MAIN_CONTENT_TITLE = "Choose a folder to scan:";

    private Label filePathLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarTitle(SUMMARY_BAR_TITLE_HEADER, SUMMARY_BAR_TITLE_PREVIEW, false, false);
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE);
        setContentTitle(MAIN_CONTENT_TITLE);
        hideBackButton();
        hideCancelButton();
        disableNextButton();
        setContent(getMainContent());
    }

    private Node getMainContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../layouts/ChooseFolderToScanContent.fxml"));               // TODO: replace with static config reference
            GridPane root = loader.load();
            ObservableList<Node> rootChildren = root.getChildren();
            AnchorPane filePathBox = (AnchorPane) rootChildren.get(0);
            ObservableList<Node> filePathBoxChildren = filePathBox.getChildren();
            this.filePathLabel = (Label) filePathBoxChildren.get(0);
            return root;
        } catch (IOException e) {
            e.printStackTrace(); // TODO: error handling
        }
        return new Label("Error loading content");
    }
}
