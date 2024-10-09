package alejandro.controller;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;


import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Stack;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JFileChooser;

import alejandro.model.FileU;
import alejandro.services.FileServiceF.FileService;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

import com.grpc.Sincronizacion;
import com.grpc.Syncronization;

public class MainController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label lblRuta;

    @FXML
    private Label folderLabel;

    @FXML
    private TextField searchField;
    
    
    @FXML
    private TreeView<String> folderTreeView;

    @FXML
    private TableView<Object> contentTableView;

    @FXML
    private TableColumn<Object, String> nameColumn;

    @FXML
    private Button addFileButton;

    @FXML
    private Button addFolderButton;
    @FXML
    private Button volverButton;
    
    @FXML
    private Label userLabel;
    @FXML
    private Button openSharedButton;
    @FXML
    private Button syncButton;

  
    private Stack<String> pilaRutas = new Stack<>();
    private String folderPath = "D://LocalFiles"; 
    private FileService fileService = new FileService();
    private FileU file  = new FileU();
    

    public void initialize() throws IOException {
        
        
        folderPath= getLocalFolder();
        lblRuta.setText(folderPath);
        loadFilesAndFolders(folderPath);
        volverButton.setVisible(false);
        initializeGridPane();
        Image image = new Image(getClass().getResourceAsStream("/icons/addfile.png"));
        ImageView imageView = new ImageView(image);

        Image imageshared = new Image(getClass().getResourceAsStream("/icons/sharedicon.png"));
        ImageView sharedicon = new ImageView(imageshared);

        Image imageFolder = new Image(getClass().getResourceAsStream("/icons/addFolder.png"));
        ImageView imageViewF = new ImageView(imageFolder);

        imageView.setFitWidth(24);
        imageView.setFitHeight(24);

        imageViewF.setFitWidth(24); 
        imageViewF.setFitHeight(24);

        sharedicon.setFitWidth(24); 
        sharedicon.setFitHeight(24);
        
        addFileButton.setGraphic(imageView);
        openSharedButton.setGraphic(sharedicon);
        addFolderButton.setGraphic(imageViewF);
        addFileButton.setOnAction(event -> openFileChooser());
        addFolderButton.setOnAction(event -> openCreateFolderModal());
        openSharedButton.setOnAction(event -> openSharedFolder());
        
        //evento boton sincronizar
        syncButton.setOnAction(event -> syncFiles());
    }
        
    private void openSharedFolder()
    {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SharedFolder.fxml"));
            Parent root = fxmlLoader.load();
    
           
            Stage stage = new Stage();
            stage.setTitle("Compartidos");
            stage.setMinWidth(500);
            stage.setMinHeight(300);
            stage.setScene(new Scene(root)); 
            stage.show();     
        } catch (IOException e) {
            e.printStackTrace();
        }
    }     
    
   //metodo para sincronizar
    private void syncFiles()
    {
        Sincronizacion sincro = new Sincronizacion();
        String resultping  = sincro.ping();
        System.out.println("ping:" + resultping);
        try {
            sincro.sincronizar();
            folderPath = "D://LocalFiles";
            loadFilesAndFolders(folderPath);    
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // esto es pa recargar la pagina apenas sincronice lo comente pq se queda pegado antes de que carge la vista, pero creo que es normal XD
    }   

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("All Files", "*.*"));

        // Abre el explorador de archivos
        Stage stage = (Stage) addFileButton.getScene().getWindow();
        java.io.File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
        }
        uploadFile(folderPath, selectedFile);
        loadFilesAndFolders(folderPath);
    }
    
    private void openCreateFolderModal() {
        try {
            // Cargar el modal desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/ModalFolder.fxml")));
            

            Parent parent = loader.load();

            // Crear una nueva ventana (Stage) para el modal
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Agregar Carpeta");


            stage.setMinWidth(400); // Ancho mínimo en píxeles
            stage.setMinHeight(200);
            stage.setScene(new Scene(parent));

            // Obtener el controlador del modal
            CreateFolderModalController controller = loader.getController();

            // Mostrar el modal y esperar hasta que el usuario lo cierre
            stage.showAndWait();
            
            // Procesar el nombre de la carpeta cuando el usuario confirme
            if (controller.isConfirmed()) {
                String folderName = controller.getFolderName();
                createFolder(folderPath,folderName);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
    private void openRenameFolderModal(String path) {
        try {
            // Cargar el modal desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/ModalRenameFolder.fxml")));
            

            Parent parent = loader.load();

            // Crear una nueva ventana (Stage) para el modal
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Renombrar carpeta");


            stage.setMinWidth(400); // Ancho mínimo en píxeles
            stage.setMinHeight(200);
            stage.setScene(new Scene(parent));

            // Obtener el controlador del modal
            RenameFolderModalController controller = loader.getController();

            // Mostrar el modal y esperar hasta que el usuario lo cierre
            stage.showAndWait();
            
            // Procesar el nombre de la carpeta cuando el usuario confirme
            if (controller.isConfirmed()) {
                String folderName = controller.getFolderName();
                renameFolder(path, folderName);
                loadFilesAndFolders(folderPath);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void openRenameFileModal(String path) {
        try {
            // Cargar el modal desde el archivo FXML
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/ModalRenameFile.fxml")));
            

            Parent parent = loader.load();

            // Crear una nueva ventana (Stage) para el modal
            Stage stage = new Stage();

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Renombrar archivo");


            stage.setMinWidth(400); // Ancho mínimo en píxeles
            stage.setMinHeight(200);
            stage.setScene(new Scene(parent));

            // Obtener el controlador del modal
            RenameFileModalController controller = loader.getController();

            // Mostrar el modal y esperar hasta que el usuario lo cierre
            stage.showAndWait();
            
            // Procesar el nombre de la carpeta cuando el usuario confirme
            if (controller.isConfirmed()) {
                String folderName = controller.getFolderName();
                renameFile(path, folderName);
                loadFilesAndFolders(folderPath);
                
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void initializeGridPane() {
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        
        int numCols = 6;
        int numRows = 5; 
    
        for (int col = 0; col < numCols; col++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(130); 
            //columnConstraints.setPrefHeight(70);
            columnConstraints.setHalignment(HPos.CENTER);
            gridPane.getColumnConstraints().add(columnConstraints);
        }
    
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(70);
            gridPane.getRowConstraints().add(rowConstraints);
        }
    
        gridPane.setHgap(20);
        gridPane.setVgap(20); 
    }
    
    private void loadFilesAndFolders(String path) {
        gridPane.getChildren().clear(); 
        try {
            
            double buttonWidth = 100;
            double buttonHeight = 90;

           
            java.io.File folder = new java.io.File(path);
            java.io.File[] files = folder.listFiles(); 

            if (files != null) {
                int column = 0;
                int row = 0;

                
                for (java.io.File file : files) {

                    
                    Button fileButton = new Button(file.getName());

                    ImageView icon;
                    if (file.isDirectory()) {
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
                    MenuItem renombrarItem = new MenuItem("Renombrar");
                    MenuItem eliminarItem = new MenuItem("Eliminar");
                    MenuItem copiarItem = new MenuItem("Copiar");

                    copiarItem.setOnAction(event->{
                        if (file.isDirectory()) {
                            copyFile(file.getAbsolutePath(), "D:/Dataset");
                        }
                        else{
                            copyFile(file.getAbsolutePath(), "D:/Dataset");
                        }

                    });
                    renombrarItem.setOnAction(event -> {
                        if (file.isDirectory()) {
                            openRenameFolderModal(file.getAbsolutePath());
                        }else{
                            openRenameFileModal(file.getAbsolutePath());
                        }
                    });

                    eliminarItem.setOnAction(event -> {
                        if (file.isDirectory()) {
                        System.out.println("Eliminada la carpeta " + file.getName());
                            deleteFolder(file.getAbsolutePath());
                            loadFilesAndFolders(folderPath);
                        
                        }else {
                            System.out.println("Eliminado el archivo " + file.getName());
                            deleteFile(file.getAbsolutePath());
                            loadFilesAndFolders(folderPath);
                        }
                    });

                    // acá se añaden los archivos/carpetas al grid.
                    contextMenu.getItems().addAll( renombrarItem, eliminarItem,copiarItem);

                    fileButton.setOnContextMenuRequested(event -> {
                        
                        contextMenu.show(fileButton, event.getScreenX(), event.getScreenY());
                    });

                    // abrir una carpeta con el botón izquierdo
                    if (file.isDirectory()) {
                        fileButton.setOnAction(event -> {
                            pilaRutas.push(folderPath);
                            folderPath = file.getAbsolutePath();
                            lblRuta.setText(folderPath);
                            volverButton.setVisible(true);
                            loadFilesAndFolders(folderPath);
                        });
                    }
                    gridPane.add(fileButton, column, row);

                    column++;
                    if (column == 6) { 
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar archivos y carpetas: " + e.getMessage());
            e.printStackTrace();
        }
    }



    
    @FXML  
    private void startLocalFolder(){
        String username = "jesus";
        String localFolder=getLocalFolder();
        if(localFolder.equals("error")){
            System.out.println("Error al crear la carpeta local");
        }else{
            String userLocalFolder =getUserHomeFolder(localFolder,username);
            if(!localFolder.equals("error")){
            System.out.println("Carpeta de archivos en: "+ userLocalFolder);
            System.out.println("Carpeta home de " + username+ " en: " + userLocalFolder);
            }else{
                System.out.println("error al crear la carpeta local del usuario");
            }
        }
        
        
    }
    
  
   public String getLocalFolder(){

       String disk= "D:\\"; 
       File folder = new File(disk+"LocalFiles");

        // Verificar si la carpeta existe
        if (folder.exists() && folder.isDirectory()) {
            return disk+"LocalFiles";
        } else {
            
            try {
                Path localPath = Paths.get(disk+"LocalFiles");
                Files.createDirectory(localPath);
                
                return localPath.toAbsolutePath().toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }    
        }
    }
    
    public String getUserHomeFolder(String path,String username){
        File folder = new File(path+"\\"+username);

        if (folder.exists() && folder.isDirectory()) {
            return path+"\\"+username;
        }else{
            
            try {
                // Ruta relativa para crear la carpeta
                Path localPath = Paths.get(path+"\\"+username);

                Files.createDirectory(localPath);


                return localPath.toAbsolutePath().toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
    }

    public void uploadFile(String path, File file) {
        // Convertir la ruta de destino en un objeto Path
        Path destinationPath = Paths.get(path);

        // Verificar si el archivo de origen existe
        if(file!=null){
            if (!file.exists()) {
                System.out.println("El archivo no existe: " + file.getAbsolutePath());
                return;
            }
        

            //acá si no existe la ruta la crea.
            if (!Files.isDirectory(destinationPath)) {
                System.out.println("La ruta de destino no es un directorio o no existe: " + path);
                return;
            }

            try {
                Path sourcePath = file.toPath(); // Convertir File a Path
                Path targetPath = destinationPath.resolve(file.getName()); // Añadir el nombre del archivo al destino

                //para copiar
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Archivo subido correctamente a: " + targetPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al subir el archivo.");
            }
        }
    }
    
    @FXML  
    public void selectFile() {
        String username = "jesus";
        String localFolder=getLocalFolder();
        String path= getUserHomeFolder(localFolder,username); // Esto luego se cambiara para que obtenga la ruta actual del lblRuta.getText()
        // Crear un JFileChooser para seleccionar el archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo");

        // Mostrar el diálogo de selección de archivo
        int result = fileChooser.showOpenDialog(null);

        // Verificar si el usuario seleccionó un archivo
        if (result == JFileChooser.APPROVE_OPTION) {
            // Obtener el archivo seleccionado
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Archivo seleccionado: " + selectedFile.getAbsolutePath());
            uploadFile(path,selectedFile);
        } else {
            System.out.println("No se seleccionó ningún archivo.");
            
        }
    }
    

    public void copyFile(String origin, String destination) {
        try {
            Path sourcePath = Paths.get(origin);
            Path destinationPath = Paths.get(destination);
    
            if (Files.isDirectory(sourcePath)) {
                
                copyDirectory(sourcePath, destinationPath);
            } else {
                
                Files.copy(sourcePath, destinationPath.resolve(sourcePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            }
    
            System.out.println("Archivo copiado de " + sourcePath.toString() + " a " + destinationPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al copiar el archivo: " + e);
        }
    }


    private void copyDirectory(Path source, Path destination) throws IOException {
        if (!Files.exists(destination)) {
            Files.createDirectories(destination);
        }

        try (var stream = Files.walk(source)) {
            stream.forEach(sourcePath -> {
                Path targetPath = destination.resolve(source.relativize(sourcePath));
                try {
                    if (Files.isDirectory(sourcePath)) {
                        if (!Files.exists(targetPath)) {
                            Files.createDirectory(targetPath);
                        }
                    } else {
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    
    @FXML 
    public void testCopy(){
        copyFile("D:\\LocalFiles\\jesus\\Prueba.txt","D:\\LocalFiles\\luis\\Prueba.txt");
    }
    
    public void deleteFile(String path) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Archivo borrado: " + filePath.toString());
            } else {
                System.out.println("El archivo no existe: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al borrar el archivo.");
        }
    }
    
    @FXML 
    public void testDelete(){
        deleteFile("D:\\LocalFiles\\luis\\Borrable.txt");
    }
    
    public void createFolder(String path, String name) {
        try {
            // Crear la ruta completa donde se creará la carpeta
            Path folderPath = Paths.get(path, name);

            // Verificar si la carpeta ya existe
            if (!Files.exists(folderPath)) {
                // Crear la carpeta
                Files.createDirectory(folderPath);
                System.out.println("Carpeta creada: " + folderPath.toString());
                
                loadFilesAndFolders(path);
            } else {
                System.out.println("La carpeta ya existe: " + folderPath.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al crear la carpeta.");
        }
    }
    
    @FXML 
    public void testCreateFolder(){
        String path= "D:\\LocalFiles\\jesus";
        String folderName ="fotos";
        createFolder(path,folderName);
    }
    
    public void copyFolder(String origin, String destination) {
        Path sourcePath = Paths.get(origin);
        Path destinationPath = Paths.get(destination);

        try {
            // Verificar si la carpeta de origen existe
            if (Files.exists(sourcePath)) {
                // Caminar a través de la estructura de archivos y copiar cada archivo y carpeta
                Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        // Crear el directorio en el destino
                        Path targetPath = destinationPath.resolve(sourcePath.relativize(dir));
                        if (!Files.exists(targetPath)) {
                            Files.createDirectories(targetPath);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Copiar cada archivo a la nueva ubicación
                        Path targetPath = destinationPath.resolve(sourcePath.relativize(file));
                        Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Carpeta copiada exitosamente de " + origin + " a " + destination);
            } else {
                System.out.println("La carpeta de origen no existe.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al copiar la carpeta.");
        }
    }
    
    @FXML 
    public void testCopyFolder(){
        String origin= "D:\\LocalFiles\\jesus\\fotos";
        String destination ="D:\\LocalFiles\\luis\\fotoscopia";
        copyFolder(origin, destination);
    }
    
    
    public void deleteFolder(String path) {
        Path folderPath = Paths.get(path);

        try {
            // Verificar si la carpeta existe
            if (Files.exists(folderPath)) {
                // Caminar a través de la estructura de archivos y borrar cada archivo y carpeta
                Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // Borrar cada archivo encontrado
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // Borrar el directorio después de borrar sus archivos
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                System.out.println("Carpeta borrada exitosamente: " + path);
            } else {
                System.out.println("La carpeta no existe: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al borrar la carpeta.");
        }
    }
    @FXML 
    public void volver(){
        
        if(!folderPath.equals(getLocalFolder())){
        folderPath= pilaRutas.pop();
        if(folderPath.equals(getLocalFolder())){
            volverButton.setVisible(false);
        }
        lblRuta.setText(folderPath);
        loadFilesAndFolders(folderPath);
        
        }else{
            System.out.println("no puedes retroceder mas");
        }
    }
    
    public void renameFile(String path, String nombreNuevo) {
        Path sourcePath = Paths.get(path);
        Path targetPath = sourcePath.resolveSibling(nombreNuevo);

        try {
            // Verificar si el archivo original existe
            if (Files.exists(sourcePath)) {
                // Renombrar el archivo
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Archivo renombrado exitosamente a: " + targetPath.toString());
            } else {
                System.out.println("El archivo no existe: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al renombrar el archivo.");
        }
    }
    
    public void renameFolder(String path, String nombreNuevo) {
        Path sourcePath = Paths.get(path);
        Path targetPath = sourcePath.resolveSibling(nombreNuevo);

        try {

            if (Files.exists(sourcePath) && Files.isDirectory(sourcePath)) {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Carpeta renombrada exitosamente a: " + targetPath.toString());
            } else {
                System.out.println("La carpeta no existe: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al renombrar la carpeta.");
        }
    }
    
    
}
