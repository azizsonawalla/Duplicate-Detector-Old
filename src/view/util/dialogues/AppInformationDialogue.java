package view.util.dialogues;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AppInformationDialogue {

    private final Alert alert;

    public AppInformationDialogue(String title, String header, String msg) {
        this.alert = new Alert(Alert.AlertType.INFORMATION);
        this.alert.setTitle(title);
        this.alert.setHeaderText(header);
        this.alert.setContentText(msg);
    }

    public boolean getConfirmation() {
        Optional<ButtonType> response = this.alert.showAndWait();
        return response.get() == ButtonType.OK;
    }
}
