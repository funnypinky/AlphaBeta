/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.view;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author shaesler
 */
public class ProgressForm {

    private final Stage dialogStage;
    private final ProgressBar pb = new ProgressBar();
    private Label status;
    
    

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        
        // PROGRESS BAR
        pb.setProgress(-1F);
        pb.setMinWidth(512);
        final VBox hb = new VBox();
        status = new Label("Lade DICOM-Dateien...");
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pb,status);
        Scene scene = new Scene(hb);
        scene.getStylesheets().addAll(this.getClass().getResource("ModernTheme.css").toExternalForm());
        dialogStage.setTitle(null);
        dialogStage.setScene(scene);
    }

    public void activateProgressBar(final Task task) throws InterruptedException {
        pb.progressProperty().bind(task.progressProperty());
        status.textProperty().bind(task.messageProperty());
        dialogStage.titleProperty().bind(task.messageProperty());
        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }

}
