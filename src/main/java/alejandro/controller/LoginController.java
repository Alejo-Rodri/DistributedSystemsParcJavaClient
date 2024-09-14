package alejandro.controller;

import alejandro.controller.repository.GoSocket;
import alejandro.controller.repository.GrpcClient;
import alejandro.model.Services;
import alejandro.model.domain.User;
import alejandro.utils.Environment;
import alejandro.utils.Logs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Services services;
    //private GrpcClient grpcClient;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        GrpcClient grpcClient = new GrpcClient(Environment.getInstance().getVariables().get("GRPC_IP"),
        Environment.getInstance().getVariables().get("GRPC_PORT"));

        String jwt = grpcClient.authenticate(username, password);
        //grpcClient.ping();
        try {
            grpcClient.shutdown();
        } catch (Exception e) {
            Logs.logWARNING(this.getClass(), "ERROR", e);
        }

        if (!jwt.equals("no")) {
            MainController.setUser(new User(username, username, password, jwt));
            services = new Services();
            GoSocket goSocket = new GoSocket(services);
            goSocket.setUsername(username);
            goSocket.start();
            loadNextScene();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("LoginError :(");
            alert.setHeaderText("Login Failed");
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }
    }

    private void loadNextScene() {
        try {
            FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/MainView.fxml")));
            Parent mainRoot = mainLoader.load();

            MainController mainController = mainLoader.getController();
            mainController.setServices(services);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(mainRoot, 800, 600));
        } catch (IOException exception) {
            Logs.logWARNING(this.getClass().getName(), "Failed while loading " + "/MainView.fxml" + " scene.", exception);
        }
    }
}
