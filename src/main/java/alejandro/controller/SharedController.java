package alejandro.controller;

import alejandro.model.FileU;
import alejandro.model.User;
import alejandro.services.FileServiceF.FileService;
import alejandro.services.UserServiceF.UserService;
import alejandro.utils.Environment;
import alejandro.utils.Logs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.gson.Gson;

public class SharedController {

    @FXML
    private TextField usernameField;
      
    @FXML
    private GridPane gridShared;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;

    @FXML
    private Button backButton;


    private UserService userService = new UserService();
    
    private FileService fileService = new FileService();
    
    private User user = new User();
    
    public void initialize() throws IOException {
        fileService.getSharedFiles();
        initializeGridPane();
        fetchAndLoadSharedFiles();
        backButton.setOnAction(event -> openMainView());
    }

    private void openMainView() {
        try {
            // Cargar la nueva vista
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Main");
            stage.setMinWidth(500);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root)); 
            stage.show();     
    
            // Cerrar la ventana actual
            Stage currentStage = (Stage) root.getScene().getWindow(); // Obtener el stage actual
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    private void initializeGridPane() {
        gridShared.getColumnConstraints().clear();
        gridShared.getRowConstraints().clear();
        
        int numCols = 6;
        int numRows = 5; 
    
        for (int col = 0; col < numCols; col++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(130); 
            //columnConstraints.setPrefHeight(70);
            columnConstraints.setHalignment(HPos.CENTER);
            gridShared.getColumnConstraints().add(columnConstraints);
        }
    
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(70);
            gridShared.getRowConstraints().add(rowConstraints);
        }
    
        gridShared.setHgap(20);
        gridShared.setVgap(20); 
    }

    private void loadSharedFiles(List<FileU> sharedFiles) {
        gridShared.getChildren().clear();    
        double buttonWidth = 100;
        double buttonHeight = 90;
    

        int column = 0;
        int row = 0;
    

        for (FileU file : sharedFiles) {
          
            Button fileButton = new Button(file.getName());
    
            ImageView icon;
            if (file.getMimeType().contains("image")) {
                
                icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_file.png")));
            } else if (file.getMimeType().contains("folder")) {
                
                icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/foldericonr.png")));
            } else {
                
                icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/bluefile.png")));
            }
            icon.setFitWidth(32);
            icon.setFitHeight(32);
            fileButton.setGraphic(icon);
            fileButton.setPrefWidth(buttonWidth);
            fileButton.setPrefHeight(buttonHeight);
            fileButton.setStyle("-fx-background-color: #536493; -fx-text-fill: white;");


            ContextMenu contextMenu = new ContextMenu();
            fileButton.setOnContextMenuRequested(event -> {
                contextMenu.show(fileButton, event.getScreenX(), event.getScreenY());
            });
    
            gridShared.add(fileButton, column, row);
            column++;

            if (column == 5) { 
                column = 0;
                row++;
            }
        }
    }
    
    public void fetchAndLoadSharedFiles() {
        try {
            List<FileU> sharedFiles = fileService.getSharedFiles();
            loadSharedFiles(sharedFiles);
        } catch (Exception e) {
            System.out.println("Error al obtener archivos compartidos: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
