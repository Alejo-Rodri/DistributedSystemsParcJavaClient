package alejandro.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateFolderModalController {
     @FXML
    private TextField folderNameField;
    
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;

    private String folderName;
    private boolean confirmed = false;

    @FXML
    private void onCreateFolder() {
        folderName = folderNameField.getText();
        if (!folderName.trim().isEmpty()) {
            confirmed = true;
            closeModal();
        } else {
            // Aquí puedes agregar una alerta o indicación de que el campo no debe estar vacío.
            System.out.println("El nombre de la carpeta no puede estar vacío.");
        }
    }


    public void initialize() {
        createButton.setStyle("-fx-background-color: #536493; -fx-text-fill: white;");
        cancelButton.setStyle("-fx-background-color: #EF5A6F ; -fx-text-fill: white;");
    }



    @FXML
    private void onCancel() {
        confirmed = false;
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) folderNameField.getScene().getWindow();
        stage.close();
    }

    public String getFolderName() {
        return folderName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
