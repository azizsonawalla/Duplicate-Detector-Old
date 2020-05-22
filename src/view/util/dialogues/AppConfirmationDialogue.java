package view.util.dialogues;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AppConfirmationDialogue {

    private final Alert alert;

    public AppConfirmationDialogue(String title, String header, String msg) {
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        this.alert.setTitle(title);
        this.alert.setHeaderText(header);
        this.alert.setContentText(msg);
    }

    public boolean getConfirmation() {
        Optional<ButtonType> response = this.alert.showAndWait();
        return response.get() == ButtonType.OK;
    }
}
