package alejandro.controller;

import alejandro.model.FileU;
import alejandro.model.User;
import alejandro.model.userSingleton;
import alejandro.services.FileServiceF.FileService;
import alejandro.services.UserServiceF.UserService;
import alejandro.utils.Environment;
import alejandro.utils.Logs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;

    private UserService userService = new UserService();
    private FileService fileService = new FileService();

    private void loadNextScene() {
        try {
            // Cargar la vista principal
            FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/MainView.fxml")));
            Parent mainRoot = mainLoader.load();

            // Obtener el controlador de la siguiente vista
            MainController mainController = mainLoader.getController();

            // Configurar servicios en el controlador (si es necesario)
            // mainController.setServices(services);

            // Cambiar la escena del Stage actual
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(mainRoot, 800, 600));
        } catch (IOException exception) {
            Logs.logWARNING(this.getClass().getName(), "Failed while loading " + "/MainView.fxml" + " scene.", exception);
        }       
    }

    private User user = new User();


    @FXML
    private void handleLoginButton() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

    
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Por favor, completa todos los campos.");
            return;
        }
        
        String token = signIn(username, password);
        if (token != null) {
            
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

            try {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
                Parent root = fxmlLoader.load();
                saveTokenToFile(token);
                Stage stage = new Stage();
                stage.setTitle("Main View");
                stage.setScene(new Scene(root)); 
                stage.show(); 
        
                //fileService.getSharedFiles();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
        if (token != null && !token.isEmpty()) {
            userSingleton.setUsername(username);
            System.out.println("Login exitoso. Token: " + token);
        } else {
            System.out.println("Login fallido.");
        }
    }



    private String signIn(String username, String password) {
        try {
            String response = userService.login(username, password);
            
            System.out.println("Response from login: " + response);
            //Gson gson = new Gson();
            User user = new User();
            user.setToken(response);
            String token = user.getToken();
            System.out.println("Token capturado del usuario: " + user.getToken());
            return token;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void saveTokenToFile(String token) {
        try {
            FileWriter writer = new FileWriter("nosoyeltoken.txt");
            writer.write(usernameField.getText());
            writer.close();
            System.out.println("El token se ha guardado correctamente en token.txt");
        } catch (IOException e) {
            System.out.println("Error al guardar el token en el archivo.");
            e.printStackTrace();
        }
    }
        
}
