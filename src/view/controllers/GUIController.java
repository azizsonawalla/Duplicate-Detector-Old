package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.async.threadPool.AppThreadPool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.DuplicateDetectorGUIApp;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

    final DuplicateDetectorGUIApp app;
    private GUIController nextController;                                                                               // Controller to switch to when 'Next' is clicked

    @FXML private Button settingsButton, backButton, nextButton, cancelButton;
    @FXML private Label navBarTitle, summaryBarSubtitle, mainContentTitle;
    @FXML private Text summaryBarTitleHead, summaryBarTitlePrev;
    @FXML private GridPane mainContent;

    public GUIController(DuplicateDetectorGUIApp app) {
        this.app = app;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFonts();
    }

    /**
     * Sets the title for the navigation bar
     * @param title new title for navigation bar
     */
    void setNavBarTitle(String title) {
        navBarTitle.setText(title);
    }

    /**
     * Sets the title for the summary bar
     * @param header part of the title to put before the colon
     * @param previewText part of the title to put after the colon
     */
    void setSummaryBarTitle(String header, String previewText, Boolean addColon, Boolean addSpace) {
        summaryBarTitleHead.setText(addColon? header + ":" : header);
        summaryBarTitlePrev.setText(addSpace? " " + previewText: previewText);
    }

    /**
     * Sets the subtitle for the summary bar
     * @param subtitle new subtitle for summary bar
     */
    void setSummaryBarSubtitle(String subtitle) {
        summaryBarSubtitle.setText(subtitle);
    }

    /**
     * Sets the main content title
     * @param title new title for content
     */
    void setContentTitle(String title) {
        mainContentTitle.setText(title);
    }

    /**
     * Inserts the given node in the main content window of the parent frame
     * @param content content to be inserted
     */
    void setContent(Node content) {
        mainContent.add(content,0, 3);
    }

    void hideSettingsButton() {
        settingsButton.setVisible(false);
    }

    void hideBackButton() {
        backButton.setVisible(false);
    }

    void hideCancelButton() {
        cancelButton.setVisible(false);
    }

    void enableCancelButton() {
        cancelButton.setDisable(false);
    }

    void disableCancelButton() {
        cancelButton.setDisable(true);
    }

    void enableNextButton() {
        if (this.nextController != null) {                                                                              // must set next controller before enabling button
            nextButton.setDisable(false);
        }
    }

    void disableNextButton() {
        nextButton.setDisable(true);
    }

    void setNextButtonText(String text) {
        nextButton.setText(text);
    }

    void setNextController(GUIController nextController) {
        this.nextController = nextController;
    }

    private void setNextButtonOnAction() {
        nextButton.setOnAction(event -> app.switchScene(this.nextController));
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("../assets/fonts/HelveticaNeueLTCom_Lt.ttf"), 16);
    }

    private void openSettingsDialogue() {
        // TODO: implement this. Set it as onClick event for settings button.
        throw new NotImplementedException();
    }

    private void goBackToPreviousDialogue() {
        // TODO: implement this. Set it as onClick event for back button.
        throw new NotImplementedException();
    }
}