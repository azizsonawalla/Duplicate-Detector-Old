package view.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseFolderToScan extends ParentController {

    private String NAV_BAR_TITLE = "Start a new scan";
    private String SUMMARY_BAR_TITLE_HEADER = "No folder selected";
    private String SUMMARY_BAR_TITLE_PREVIEW = "";
    private String SUMMARY_BAR_SUBTITLE = "0 files found";
    private String MAIN_CONTENT_TITLE = "Choose a folder to scan:";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setNavBarTitle(NAV_BAR_TITLE);
        setSummaryBarTitle(SUMMARY_BAR_TITLE_HEADER, SUMMARY_BAR_TITLE_PREVIEW, false, false);
        setSummaryBarSubtitle(SUMMARY_BAR_SUBTITLE);
        setContentTitle(MAIN_CONTENT_TITLE);
        setContent(getMainContent());
    }

    private Node getMainContent() {
        Node root = new Label("Error loading content");
        try {
            root = FXMLLoader.load(getClass().getResource("../layouts/ChooseFolderToScanContent.fxml"));
        } catch (IOException e) {
            e.printStackTrace(); // TODO: error handling
        }
        return root;
    }
}
