package alejandro.controller;

import alejandro.model.FileU;
import alejandro.model.User;
import alejandro.services.FileServiceF.FileService;
import alejandro.services.UserServiceF.UserService;
import alejandro.utils.Environment;
import alejandro.utils.Logs;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
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
import com.grpc.Syncronization;
import java.util.Stack;
import javafx.scene.control.Label;

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
    @FXML
    private Label lblRuta;


    private UserService userService = new UserService();
    
    // controla la ruta que aparece en el texto
    private Stack<String> pilaRutas = new Stack<>();
    // controla los fingerprints
    private Stack<String> pilaFingerprints = new Stack<>();
    private FileService fileService = new FileService();

     String target = "10.153.91.133:50052";
     ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

    private Syncronization sync = new Syncronization(channel);
    
    private User user = new User();
    private String folderPath = "SharedFiles"; 
    
    public void initialize() throws IOException {
        lblRuta.setText(folderPath);
        backButton.setVisible(false);
        fileService.getSharedFiles();
        initializeGridPane();
        fetchAndLoadSharedFiles();
        backButton.setOnAction(event -> retroceder());
    }

    private void retroceder(){
        // retroceder en la ruta de la vista
        if(!pilaRutas.isEmpty()){
            folderPath=pilaRutas.pop();
            lblRuta.setText(folderPath);
        // pop + peek
        // desapila el fingerprint
        pilaFingerprints.pop();
        // checkea la anterior carpeta
        // primero verifica si hay carpeta que checkear, sino significa q no se vuelve al principio
       // si quedo vacia significa que vuelve al inicio
        if(pilaFingerprints.isEmpty()){
            
            // cargar inicio
            initializeGridPane();
            backButton.setVisible(false);
            fetchAndLoadSharedFiles();
        }
        // sino abre la carpeta donde cayo
        else{
            
            // abrir carpeta anterior
            openFolder(pilaFingerprints.peek());
        }
        }
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
            icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/bluefile.png")));
        } else if (file.getMimeType().contains("dir")) {
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

        // Crear el menú contextual
        ContextMenu contextMenu = new ContextMenu();

        // Crear la opción de abrir
        MenuItem openItem = new MenuItem("Abrir");
        openItem.setOnAction(event -> {
            if (file.getMimeType().contains("dir")) {
                System.out.println("Carpeta : " + file.getName());
                openFolder(file.getId());
                //aca se guarda el fingerprint de la carpeta que se abrio}
                //pa volver es pop + peek, creo xd 
                pilaFingerprints.push(file.getId());
                //aca se guardan la ruta de la carpeta anterior, a la que se va a volver
                pilaRutas.push(folderPath);
                folderPath= folderPath+"/"+file.getName();
                lblRuta.setText(folderPath);
                
            } else {
                System.out.println("Archivo : " + file.getName());
            }
            // Aquí puedes agregar más lógica para abrir el archivo o la carpeta si es necesario.
        });

        // Crear la opción de descargar
        MenuItem downloadItem = new MenuItem("Descargar");
        downloadItem.setOnAction(event -> downloadSharedFile(file));  // Llama a la función de descarga

        // Crear la opción de eliminar
        MenuItem deleteItem = new MenuItem("Eliminar");
        deleteItem.setOnAction(event -> deleteSharedFile(file));

        // Añadir los ítems al menú contextual
        contextMenu.getItems().addAll(openItem, downloadItem, deleteItem);

        // Mostrar el menú contextual al hacer clic derecho
        fileButton.setOnContextMenuRequested(event -> {
            contextMenu.show(fileButton, event.getScreenX(), event.getScreenY());
        });

        // Añadir el botón al grid
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
    
    public void openFolder(String fingerPrint){
        
        try {
            initializeGridPane();
            backButton.setVisible(true);
            List<FileU> sharedFiles = fileService.getFilesInFolder(fingerPrint);
            loadSharedFiles(sharedFiles);
            
        } catch (Exception e) {
            System.out.println("Error al obtener archivos compartidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void downloadSharedFile(FileU file) {
        try {
            String fingerprint = file.getId(); 
            String fileName = file.getName();   
            String fullFilePath = "D:\\LocalFiles\\" + fileName;  
            sync.download(fullFilePath, fingerprint);
            System.out.println("Descarga completada: " + fileName);
        } catch (Exception e) {
            System.out.println("Error al descargar el archivo: " + file.getName());
            e.printStackTrace();
        }
    }
    public void deleteSharedFile(FileU file) {
    try {
        String folderFingerprint = "" ; 
        // si se esta detro de una carpeta se pasa el folderhash de esa carpeta
        if(!pilaFingerprints.isEmpty()){
          folderFingerprint = pilaFingerprints.peek() ; 
        }
        
        
        
        String fileFingerprint = file.getId(); // Hash del archivo a eliminar
        
        // Llamada al servicio para eliminar el archivo
        boolean deleted = fileService.deleteSharedFile(folderFingerprint,fileFingerprint);
        
        if (deleted) {
            System.out.println("Archivo eliminado: " + file.getName());
            fileService.getSharedFiles();
            // Se recarga la vista inicial
            if(folderFingerprint.equals("")){
                initializeGridPane();
                fetchAndLoadSharedFiles();
            }else{
                //se recarga la vista en la que se esta
                openFolder(folderFingerprint);
            }
            
        } else {
            System.out.println("No se pudo eliminar el archivo: " + file.getName());
        }
    } catch (Exception e) {
        System.out.println("Error al eliminar el archivo: " + file.getName());
        e.printStackTrace();
    }
}

}
