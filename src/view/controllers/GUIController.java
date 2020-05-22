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

        Node mainWindow = loadMainWindow();
        if (mainWindow != null) {
            setMainWindow(mainWindow);
        }

        Node mainContent = loadMainContent();
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
        nextButton.setDisable(false);
    }

    void disableNextButton() {
        nextButton.setDisable(true);
    }

    void hideNextButton() {
        nextButton.setVisible(false);
    }

    void setNextButtonText(String text) {
        nextButton.setText(text);
    }

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

    void setCancelButtonOnAction(EventHandler<ActionEvent> e) {
        cancelButton.setOnAction(e);
    }

    void setCancelButtonText(String text) {
        cancelButton.setText(text);
    }

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

    void removeMainWindowLogo() {
        mainContent.getChildren().remove(0);
        int rowIdx = GridPane.getRowIndex(mainContentLogo);
        mainContent.getRowConstraints().get(rowIdx).setPercentHeight(0);
    }

    void makeMainContentFillWidth() {
        GridPane.setColumnIndex(mainContent, 0);
        int cols = mainWindow.getColumnConstraints().size();
        GridPane.setColumnSpan(mainContent, cols);
    }

    void goToNextScene() {
        app.switchScene(this.nextController);
    }

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

    private void goToFirstScene() {
        app.switchScene(new NewScan(app));
    }

    /**
     * Inserts the given node in the main content pane of the parent frame
     * @param content content to be inserted
     */
    private void setMainContent(Node content) {
        mainContent.add(content,0, 3);
    }

    private void setNextButtonOnAction() {
        nextButton.setOnAction(event -> goToNextScene());
    }

    private void setBackButtonOnAction() {
        if (this.prevController != null) {
            backButton.setOnAction(event -> goToPrevScene());
        } else {
            backButton.setOnAction(event -> goToFirstScene());
        }
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


    /**
     * Load the main content for this scene
     * @return Node holding main content
     */
    Node loadMainContent() {
        return null;                                                                                                    // sub-classes are expected to override this
    }


    /**
     * Load the main window for this scene
     * @return Node holding main window
     */
    Node loadMainWindow() {
        return null;                                                                                                    // sub-classes are expected to override this
    }

    protected abstract void cleanupSelf();
}
