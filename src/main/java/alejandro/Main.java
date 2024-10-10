package alejandro;

import alejandro.model.userSingleton;
import alejandro.utils.Logs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;                                                                                                     

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            String token = checkForToken("nosoyeltoken.txt");

            if (token != null && !token.isEmpty()) {

                FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/MainView.fxml")));
                Parent root = mainLoader.load();

                primaryStage.setTitle("Gestor de archivos");
                primaryStage.setScene(new Scene(root, 800, 600));
                primaryStage.show();
            } else {
                
                FXMLLoader loginLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/LoginScene.fxml")));
                Parent root = loginLoader.load();

                primaryStage.setTitle("Sistema de Gestión Documental");
                primaryStage.setScene(new Scene(root, 800, 600));
                primaryStage.show();
            }

        } catch (Exception exception) {
            Logs.logWARNING(this.getClass().getName(), "Error while loading root scene", exception);
        }
    }
    private String checkForToken(String filePath) {
        File tokenFile = new File(filePath);

        if (tokenFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(tokenFile))) {
                String token = reader.readLine();
                userSingleton.setToken(token);
                return token;  
            } catch (IOException e) {
                System.out.println("Error al leer el archivo de token.");
                e.printStackTrace();
            }
        } else {
            System.out.println("El archivo de token no existe.");
        }

        return null; 
    }
}
