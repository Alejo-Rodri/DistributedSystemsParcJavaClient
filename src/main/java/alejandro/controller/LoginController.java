package alejandro.controller;

import alejandro.controller.repository.GoSocket;
import alejandro.controller.repository.GrpcClient;
import alejandro.model.Services;
import alejandro.model.domain.User;
import alejandro.utils.Environment;
import alejandro.utils.Logs;
import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import javax.swing.JFileChooser;

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
    
    
    // metodo para inicializar carpeta local usuario
    
    @FXML  
    private void startLocalFolder(){
        String username = usernameField.getText();
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
        String username = usernameField.getText();
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
    public void testDeleteFolder(){
        String path= "D:\\LocalFiles\\luis\\fotoscopia";
        deleteFolder(path);
    }
    
    
    // péndiente ruta para conseguir archivos segun la ruta aun falta definir formato
    
    
    
    
    
    
    

   

     
}
