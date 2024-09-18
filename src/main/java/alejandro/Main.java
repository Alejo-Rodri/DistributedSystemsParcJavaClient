package alejandro;

import alejandro.utils.Logs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loginLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/ArchivosView.fxml")));
            Parent root = loginLoader.load();

            primaryStage.setTitle("Sistema de Gestión Documental");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (Exception exception) {
            Logs.logWARNING(this.getClass().getName(), "Error while loading root scene", exception);
        }
    }

    /*
    public static void main(String[] args) {
        launch(args);
    }
     */
}
