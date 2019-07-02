/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta;

import alphabeta.data.DatabaseAccess;
import alphabeta.view.mainViewController;
import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author shaesler
 */
public class AlphaBeta extends Application {

    public static final String DATABASEPATH = java.lang.System.getProperty("user.dir") + "\\data\\database\\";
    public static final String DATABASENAME = "alphabeta.de.db";

    private final Scene scene = new Scene(new VBox());

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(c -> {
            return new mainViewController(this);
        });
        loader.setLocation(this.getClass().getResource("view/mainView.fxml"));
        Parent root = loader.load();
        this.scene.setRoot(root);
        this.scene.getStylesheets().addAll(this.getClass().getResource("view/ModernTheme.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Plan Auswerte Tool");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        checkDB();
        launch(args);
    }

    private static void checkDB() {
        File testDB = new File(DATABASEPATH + DATABASENAME);
        if (!testDB.exists()) {
            try {
                DatabaseAccess db = new DatabaseAccess(DATABASENAME);
                new File(DATABASEPATH).mkdirs();
                db.connect();
                db.firstRun();
                db.shutdown();
            } catch (SQLException ex) {
                Logger.getLogger(AlphaBeta.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(-1);
            }
        }
    }

    public Scene getScene() {
        return scene;
    }

}
