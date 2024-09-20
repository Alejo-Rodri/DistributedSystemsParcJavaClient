package alejandro.controller;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import alejandro.controller.repository.GrpcClient;
import alejandro.controller.view.*;
import alejandro.model.FileService;
import alejandro.model.Services;
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
import alejandro.model.domain.File;
import alejandro.model.domain.Node;
import alejandro.model.domain.User;
import alejandro.utils.Environment;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MainController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Label usernameLabel;

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

    private GrpcClient grpcClient;
    private static User user;
    private Services services;
    private TreeViewController treeViewController;
    private TableViewController tableViewController;
    private ContextMenuFileController contextMenuFileController;
    private ContextMenuFolderController contextMenuFolderController;
    private FileService fileService;

    private final String folderPath = "D:/Distribuidos"; // Cambia a la carpeta que desees explorar

    public void setServices(Services services) {
        this.services = services;
        services.setTreeViewController(treeViewController);
        services.parseToTree(grpcClient.getRootJson(user.getJwt(), user.getUsername()));
        setRootNode(services.getRootNode());

        // Obtener y manejar archivos compartidos
        String[] split = grpcClient.getSharedFiles(user.getJwt()).split("\n");
        for (String json : split) {
            services.handleSocket(json);
        }
        
        // Actualizar el GridPane con los archivos
        //updateGridPaneWithFiles();
    }

    public void initialize() {
        usernameLabel.setText("User: " + getUser().getUsername());
        loadFilesAndFolders();
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
                //createFolder(folderName);
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
    
    

    /* 
    private void updateGridPaneWithFiles() {
        // Limpiar el GridPane antes de agregar nuevos elementos
        gridPane.getChildren().clear();

        // Obtener la lista de archivos desde fileService
        ObservableList<File> fileList = fileService.uploadFile("asd","asdsa",false); // Asumiendo que fileService tiene un método getFiles()

        // Definir el número de columnas
        int numCols = 6;

        // Calcular el número de filas necesario
        int numRows = (int) Math.ceil((double) fileList.size() / numCols);
        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        // Ruta a la carpeta de iconos
        String iconPath = "/icons/";

        // Crear y agregar ImageView al GridPane
        for (int i = 0; i < fileList.size(); i++) {
            FileFolderItem item = fileList.get(i);
            int row = i / numCols;
            int col = i % numCols;

            // Crear un ImageView para la celda (row, col)
            String iconName = item.getType().equals("folder") ? "icon_folder.png" : "icon_file.png";
            Image image = new Image(getClass().getResourceAsStream(iconPath + iconName));
            if (image.isError()) {
                System.err.println("Error loading image from path: " + iconPath + iconName);
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100); // Ajustar el ancho del icono
            imageView.setFitHeight(50); // Ajustar el alto del icono
            imageView.setPreserveRatio(true); // Mantener la proporción del icono

            // Agregar el ImageView al GridPane en la celda (col, row)
            gridPane.add(imageView, col, row); // Nota: los parámetros son (col, row) en lugar de (row, col)
        }
    }

    */
    public void setRootNode(Node rootNode) {
        treeViewController.setRootNode(rootNode);
        tableViewController.showContent(rootNode);
    }

    @FXML
    private void onCreateUser() {
        CreateUserController createUserController = new CreateUserController(fileService);
        createUserController.createUser();
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            SearchController searchController = new SearchController();
            ObservableList<File> searchResults = searchController.search(services.getRootNode(), query);
            tableViewController.showSearchResults(searchResults);
        }
    }

    public static User getUser() {
        if (user == null) {
           user = new User(null, null, null, null);
        }
        return user;
    }

    public static void setUser(User user) {
        MainController.user = user;
    }


    private void loadFilesAndFolders() {
        try {
            // Definir el tamaño uniforme de los botones
            double buttonWidth = 150;
            double buttonHeight = 100;
    
            // Obtener el directorio de archivos
            java.io.File folder = new java.io.File(folderPath);
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
    
                    // Añadir acción al botón (puedes modificarlo para abrir archivos o carpetas)
                    fileButton.setOnAction(event -> {
                        System.out.println("Archivo/Carpeta seleccionada: " + file.getAbsolutePath());
                        // Aquí puedes agregar más lógica para abrir el archivo o mostrar su contenido
                    });
    
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
}
