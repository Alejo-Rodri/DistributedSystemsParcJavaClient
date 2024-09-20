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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.JFileChooser;

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
    

  
    private Stack<String> pilaRutas = new Stack<>();
    private String folderPath; // Cambia a la carpeta que desees explorar

    

    public void initialize() {
        folderPath= getLocalFolder();
        lblRuta.setText(folderPath);
        loadFilesAndFolders(folderPath);
        volverButton.setVisible(false);
        
        
        initializeGridPane();
        Image image = new Image(getClass().getResourceAsStream("/icons/addfile.png"));
        ImageView imageView = new ImageView(image);

        Image imageFolder = new Image(getClass().getResourceAsStream("/icons/addFolder.png"));
        ImageView imageViewF = new ImageView(imageFolder);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);

        imageViewF.setFitWidth(18); 
        imageViewF.setFitHeight(18);
        
        addFileButton.setGraphic(imageView);
        addFolderButton.setGraphic(imageViewF);
        addFileButton.setOnAction(event -> openFileChooser());
        addFolderButton.setOnAction(event -> openCreateFolderModal());
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





    private void initializeGridPane() {
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();
        
        int numCols = 6; // Número de columnas
        int numRows = 5; // Número de filas (ajusta según lo que necesites)
    
        for (int col = 0; col < numCols; col++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPrefWidth(160); // Tamaño uniforme de columnas
            columnConstraints.setHalignment(HPos.CENTER);
            gridPane.getColumnConstraints().add(columnConstraints);
        }
    
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPrefHeight(120); // Tamaño uniforme de filas
            gridPane.getRowConstraints().add(rowConstraints);
        }
    
        gridPane.setHgap(20); // Espaciado horizontal entre botones
        gridPane.setVgap(20); // Espaciado vertical entre botones
    }
    


    private void loadFilesAndFolders(String path) {
        gridPane.getChildren().clear(); 
        try {
            // Definir el tamaño uniforme de los botones
            double buttonWidth = 150;
            double buttonHeight = 100;
    
            // Obtener el directorio de archivos
            java.io.File folder = new java.io.File(path);
            java.io.File[] files = folder.listFiles(); // Listar todos los archivos y carpetas
    
            if (files != null) {
                int column = 0;
                int row = 0;
    
                // Iterar sobre todos los archivos y carpetas
                for (java.io.File file : files) {
    
                    // Crear un botón para cada archivo o carpeta
                    Button fileButton = new Button(file.getName());
    
                    // Establecer el ícono dependiendo si es un archivo o carpeta
                    ImageView icon;
                    if (file.isDirectory()) {
                        icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/icon_file.png")));
                    } else {
                        icon = new ImageView(new Image(getClass().getResourceAsStream("/icons/addfile.png")));
                    }
    
                    // Configurar el tamaño del ícono
                    icon.setFitWidth(32);
                    icon.setFitHeight(32);
                    fileButton.setGraphic(icon);
    
                    // Establecer el mismo tamaño para todos los botones
                    fileButton.setPrefWidth(buttonWidth);   // Ancho del botón
                    fileButton.setPrefHeight(buttonHeight); // Alto del botón
    
                    // Abrir una carpeta
                    if (file.isDirectory()) {
                    fileButton.setOnAction(event -> {
                        pilaRutas.push(folderPath);
                        folderPath=file.getAbsolutePath();
                        lblRuta.setText(folderPath);
                        volverButton.setVisible(true);
                        System.out.println(file.getAbsolutePath());
                        loadFilesAndFolders(file.getAbsolutePath());
                        
                        // Aquí puedes agregar más lógica para abrir el archivo o mostrar su contenido
                    });}
                    
    
                    // Añadir el botón al GridPane
                    gridPane.add(fileButton, column, row);
    
                    // Controlar las columnas y filas
                    column++;
                    if (column == 6) { // Máximo 6 columnas por fila
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




    // metodo para inicializar carpeta local usuario
    
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
       //Verifica si ya existe la carpeta local
       String disk= "D:\\"; 
       File folder = new File(disk+"LocalFiles");

        // Verificar si la carpeta existe
        if (folder.exists() && folder.isDirectory()) {
            return disk+"LocalFiles";
        } else {
            // en caso de que no exista la crea
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
        // Verificar si la carpeta ya existe en el equipo
        if (folder.exists() && folder.isDirectory()) {
            return path+"\\"+username;
        }else{
            // en caso de que no, crearla y devolver el path
            try {
                // Ruta relativa para crear la carpeta
                Path localPath = Paths.get(path+"\\"+username);

                // Crear la carpeta
                Files.createDirectory(localPath);


                return localPath.toAbsolutePath().toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
        }
    }
    
    
    // Todas estas rutas se moveran a la vista del gestor de archivos
    
    
    // operaciones archivos (no confundir con carpetas)
    public void uploadFile(String path, File file) {
        // Convertir la ruta de destino en un objeto Path
        Path destinationPath = Paths.get(path);

        // Verificar si el archivo de origen existe
        if (!file.exists()) {
            System.out.println("El archivo no existe: " + file.getAbsolutePath());
            return;
        }

        // Verificar si la ruta de destino es un directorio y existe
        if (!Files.isDirectory(destinationPath)) {
            System.out.println("La ruta de destino no es un directorio o no existe: " + path);
            return;
        }

        // Intentar copiar el archivo al destino
        try {
            Path sourcePath = file.toPath(); // Convertir File a Path
            Path targetPath = destinationPath.resolve(file.getName()); // Añadir el nombre del archivo al destino

            // Copiar el archivo a la ruta de destino
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Archivo subido correctamente a: " + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al subir el archivo.");
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
            // Convertir las rutas de origen y destino a objetos Path
            Path sourcePath = Paths.get(origin);
            Path destinationPath = Paths.get(destination);

            // Copiar el archivo de origen a destino, reemplazar si ya existe
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Archivo copiado de " + sourcePath.toString() + " a " + destinationPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al copiar el archivo.");
        }
    }
    
    @FXML 
    public void testCopy(){
        copyFile("D:\\LocalFiles\\jesus\\Prueba.txt","D:\\LocalFiles\\luis\\Prueba.txt");
    }
    
    public void deleteFile(String path) {
        try {
            // Convertir la ruta a un objeto Path
            Path filePath = Paths.get(path);

            // Verificar si el archivo existe
            if (Files.exists(filePath)) {
                // Borrar el archivo
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
    

}
