package alejandro.controller.view;

import alejandro.controller.MainController;
import alejandro.model.FileService;
import alejandro.model.domain.File;
import alejandro.model.domain.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class ContextMenuFileController {
    private final ContextMenu fileContextMenu;
    private final FileService fileService;
    private Node currentNode;
    private File selectedFile;

    public ContextMenuFileController(FileService fileService) {
        this.fileContextMenu = new ContextMenu();
        this.fileService = fileService;
        createContextMenu();
    }

    private void createContextMenu() {
        MenuItem downloadItem = new MenuItem("Download");
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem propertiesItem = new MenuItem("Properties");

        downloadItem.setOnAction(event -> downloadFile());
        renameItem.setOnAction(event -> renameFile());
        deleteItem.setOnAction(event -> deleteFile());
        propertiesItem.setOnAction(event -> showProperties());

        fileContextMenu.getItems().addAll(downloadItem, renameItem, deleteItem, propertiesItem);
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public void show(TableRow<Object> row, MouseEvent event) {
        Object item = row.getItem();
        System.out.println("broadcast");
        if (item instanceof MainController.FileFolderItem fileFolderItem) {
            System.out.println("es item");
            System.out.println("type: " + ((MainController.FileFolderItem) item).getType());
            if (fileFolderItem.getType().equals("Archivo") || fileFolderItem.getType().equals("Carpeta")) {
                System.out.println("her");
                System.out.println("name: " + fileFolderItem.getName());
                for (File file : currentNode.getFiles()) {
                    System.out.println(file.getFileName());
                    if (file.getFileName().equals(fileFolderItem.getName())) {
                        System.out.println("and there");
                        setSelectedFile(file);
                        break;
                    }
                }
            } else {
                setSelectedFile(null);
            }
        }

        fileContextMenu.show(row, event.getScreenX(), event.getScreenY());
    }

    private void downloadFile() {
        String message;
        System.out.println(currentNode.getFullPath() + currentNode.getFiles());
        if (fileService.downloadFile(currentNode.getFullPath())) message = "Downloading file...:)";
        else message = "Couldn't download the file :c";

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void renameFile() {
        TextInputDialog dialog = new TextInputDialog(selectedFile.getFileName());
        dialog.setTitle("Renombrar archivo");
        dialog.setHeaderText("Renombrar archivo o carpeta");
        dialog.setContentText("Nuevo nombre:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get();
            fileService.renameFile(selectedFile.getFullName(), newName);
        } else {
            // Lógica para renombrar una carpeta si es necesario
            TextInputDialog dialog2 = new TextInputDialog(currentNode.getFolderName());
            dialog2.setTitle("Renombrar carpeta");
            dialog2.setHeaderText("Renombrar carpeta");
            dialog2.setContentText("Nuevo nombre:");

            Optional<String> result2 = dialog.showAndWait();
            if (result2.isPresent()) {
                String newName = result2.get();
                System.out.println(currentNode.getFullPath());
                fileService.renameFile(currentNode.getFullPath(), newName);
            }
        }
    }

    private void deleteFile() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar archivo");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar este archivo?");
        alert.setContentText("Este cambio no se puede deshacer.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            fileService.deleteFile(currentNode.getFullPath());
        }
        fileService.ping();
    }

    private void showProperties() {
        PropertiesFileController propertiesFileController = new PropertiesFileController(fileService);
        propertiesFileController.show(currentNode);
    }

    public void setSelectedFile(File file) {
        this.selectedFile = file;
    }
}
