package view.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.searchModel.ScanController;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Logger;
import view.DuplicateDetectorGUIApp;

import java.net.URL;
import java.util.ResourceBundle;

import static view.textBindings.GUIControllerText.FAILED_TO_LOAD_MAIN_CONTENT_MSG;

/**
 * An abstract controller for the GUI. Implements methods to help control UI elements common to most scenes
 */
public abstract class GUIController implements Initializable {

    final Logger log;
    final DuplicateDetectorGUIApp app;
    ScanController model;
    private GUIController prevController;
    private GUIController nextController;                                                                               // Controller to switch to when 'Next' is clicked

    @FXML private Button settingsButton, backButton, nextButton, cancelButton;
    @FXML private Label navBarTitle, summaryBarSubtitle, mainContentTitle;
    @FXML private Text summaryBarTitleHead, summaryBarTitlePrev;
    @FXML private GridPane mainContent, mainWindow, root;
    @FXML private Pane mainContentLogo, nextButtonLoadingOverlay;

    /**
     * Create GUI controller object
     * @param app the instance of the JavaFX application
     */
    GUIController(DuplicateDetectorGUIApp app) {
        this.app = app;
        this.log = new Logger(this.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.model = app.getModel();
        loadFonts();
        setBackButtonOnAction();

        Node mainWindow = app.tryWithFatalAppError(this::loadMainWindow,FAILED_TO_LOAD_MAIN_CONTENT_MSG);
        if (mainWindow != null) {
            setMainWindow(mainWindow);
        }

        Node mainContent = app.tryWithFatalAppError(this::loadMainContent,FAILED_TO_LOAD_MAIN_CONTENT_MSG);
        if (mainContent != null) {
            setMainContent(mainContent);
        }

        initCopy();
        configureControls();
    }

    /**
     * Sets the title for the navigation bar
     * @param title new title for navigation bar
     */
    void setNavBarTitle(String title) {
        navBarTitle.setText(title);
    }

    /**
     * Sets the given string as the summary bar header and uses the absolute path of the current root directory set in
     * the model as the preview text. Assumes model and root directory are not null.
     * @param header part of the title to put before the colon
     */
    void setSummaryBarHeadWithFilePath(String header) {
        setSummaryBarTitle(header, getPathToCurrentRootDir(), true, true);
    }

    /**
     * Returns the absolute path of the root directory currently set in the model. Assumes model and root directory
     * are not null.
     */
    String getPathToCurrentRootDir() {
        return model.getRootDirectories().get(0).getAbsolutePath();
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
     * Hide the Settings button
     */
    void hideSettingsButton() {
        settingsButton.setVisible(false);
    }

    /**
     * Hide the Back button
     */
    void hideBackButton() {
        backButton.setVisible(false);
    }

    /**
     * Hide the Cancel button
     */
    void hideCancelButton() {
        cancelButton.setVisible(false);
    }

    /**
     * Enable the Cancel button
     */
    void enableCancelButton() {
        cancelButton.setDisable(false);
    }

    /**
     * Disable the Cancel button
     */
    void disableCancelButton() {
        cancelButton.setDisable(true);
    }

    /**
     * Enable the Next button
     */
    void enableNextButton() {
        nextButton.setDisable(false);
    }

    /**
     * Disable the Next button
     */
    void disableNextButton() {
        nextButton.setDisable(true);
    }

    /**
     * Hide the Next button
     */
    void hideNextButton() {
        nextButton.setVisible(false);
    }

    /**
     * Set the text for the Next button
     * @param text new text for Next button
     */
    void setNextButtonText(String text) {
        nextButton.setText(text);
    }

    /**
     * Set the controller for the scene to switch to when the Next button is pressed
     * @param nextController controller for next scene
     */
    void setNextController(GUIController nextController) {
        this.nextController = nextController;
        setNextButtonOnAction();
    }

    /**
     * Set the previous scene's controller. Control will transfer to this controller when the 'Back' button is pressed
     * @param prev reference to previous controller
     */
    void setPrevController(GUIController prev) {
        this.prevController = prev;
    }

    /**
     * Set the EventHandler to be called when the Cancel button is pressed
     * @param e EventHandler for Cancel button
     */
    void setCancelButtonOnAction(EventHandler<ActionEvent> e) {
        cancelButton.setOnAction(e);
    }

    /**
     * Set the text for the Cancel button
     * @param text new text for Cancel button
     */
    void setCancelButtonText(String text) {
        cancelButton.setText(text);
    }

    /**
     * Reconfigures the Cancel button to be an Exit button
     */
    void swapCancelButtonForExitButton() {
        setCancelButtonText("Exit");
        setCancelButtonOnAction(event -> Platform.exit());
    }

    /**
     * Replaces the main window with the given node
     * @param content content to be inserted
     */
    void setMainWindow(Node content) {
        int mainWindowRowIdx = GridPane.getRowIndex(mainWindow);
        root.getChildren().remove(mainWindow);
        GridPane.setRowIndex(content, mainWindowRowIdx);
        root.getChildren().add(root.getChildren().size(), content);
    }

    /**
     * Removes the main window logo
     */
    void removeMainWindowLogo() {
        mainContent.getChildren().remove(0);
        int rowIdx = GridPane.getRowIndex(mainContentLogo);
        mainContent.getRowConstraints().get(rowIdx).setPercentHeight(0);
    }

    /**
     * Go to the next scene. Next scene controller must be set before calling.
     */
    void goToNextScene() {
        app.switchScene(this.nextController);
    }

    /**
     * Go to the previous scene. Previous scene controller must be set before calling
     */
    void goToPrevScene() {
        app.switchScene(prevController);
    }

    /**
     * Cleans-up and goes back to the previous scene
     */
    void reset() {
        cleanupSelf();
        goToFirstScene();
    }

    /**
     * Sets the EventHandlers for when the Back button is pressed
     */
    protected void setBackButtonOnAction() {
        if (this.prevController != null) {
            backButton.setOnAction(event -> goToPrevScene());
        } else {
            backButton.setOnAction(event -> goToFirstScene());
        }
    }

    /**
     * Load the main content for this scene
     * @return Node holding main content
     */
    protected Node loadMainContent() throws Exception {
        return null;                                                                                                    // sub-classes are expected to override this
    }

    /**
     * Load the main window for this scene
     * @return Node holding main window
     */
    protected Node loadMainWindow() throws Exception {
        return null;                                                                                                    // sub-classes are expected to override this
    }

    protected void cleanupSelf() {
        // TODO
    }

    private void goToFirstScene() {
        app.switchScene(new NewScan(app));
    }

    private void setMainContent(Node content) {
        mainContent.add(content,0, 3);
    }

    private void setNextButtonOnAction() {
        nextButton.setOnAction(event -> goToNextScene());
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("../assets/fonts/HelveticaNeueLTCom_Lt.ttf"), 16);
    }

    private void setSettingsButtonOnAction() {
        // TODO: implement this. Set it as onClick event for settings button.
        throw new NotImplementedException();
    }

    abstract void initCopy();

    abstract void configureControls();
}
