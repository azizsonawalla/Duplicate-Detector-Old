package view.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.URL;
import java.util.ResourceBundle;

public class ParentController implements Initializable {

    @FXML private Button settingsButton, backButton;
    @FXML private Label navBarTitle, summaryBarSubtitle, mainContentTitle;
    @FXML private Text summaryBarTitleBefore, summaryBarTitleAfter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadFonts();
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

    /**
     * Sets the title for the navigation bar
     * @param title new title for navigation bar
     */
    protected void setNavBarTitle(String title) {
        navBarTitle.setText(title);
    }

    /**
     * Sets the title for the summary bar
     * @param beforeColon part of the title to put before the colon
     * @param afterColon part of the title to put after the colon
     */
    protected void setSummaryBarTitle(String beforeColon, String afterColon) {
        summaryBarTitleBefore.setText(beforeColon + ":");
        summaryBarTitleAfter.setText(" " + afterColon);
    }

    /**
     * Sets the subtitle for the summary bar
     * @param subtitle new subtitle for summary bar
     */
    protected void setSummaryBarSubtitle(String subtitle) {
        summaryBarSubtitle.setText(subtitle);
    }

    /**
     * Sets the main content title
     * @param title new title for content
     */
    protected void setContentTitle(String title) {
        // TODO: implement
        throw new NotImplementedException();
    }
}
