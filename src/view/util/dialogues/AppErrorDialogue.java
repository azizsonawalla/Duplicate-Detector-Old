package view.util.dialogues;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AppErrorDialogue {

    public static void showError(String msg) {
        AppAlert a = new AppAlert();
        a.setMsg(msg);
        Platform.runLater(a);                                                                                           // TODO: submit to app pool instead of UI thread
    }

    private static class AppAlert implements Runnable {

        private String msg = "";

        @Override
        public void run() {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(msg);
            a.show();
        }

        void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
