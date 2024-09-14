package alejandro.controller.view;

import alejandro.model.FileService;
import alejandro.model.domain.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Optional;

public class ContextMenuFolderController {
    private final ContextMenu fileContextMenu;
    private final FileService fileService;
    private Node currentNode;

    public ContextMenuFolderController(FileService fileService) {
        this.fileContextMenu = new ContextMenu();
        this.fileService = fileService;
        createContextMenu();
    }

    private void createContextMenu() {
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem propertiesItem = new MenuItem("Properties");
        MenuItem uploadFileItem = new MenuItem("Upload File");
        MenuItem uploadFolderItem = new MenuItem("Upload Folder");

        renameItem.setOnAction(event -> renameFolder());
        deleteItem.setOnAction(event -> deleteFolder());
        propertiesItem.setOnAction(event -> showProperties());
        uploadFileItem.setOnAction(event -> uploadFile());
        uploadFolderItem.setOnAction(event -> uploadFolder());

        fileContextMenu.getItems().addAll(renameItem, deleteItem, propertiesItem, uploadFileItem, uploadFolderItem);
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public void show(Node node, TreeItem<String> item, MouseEvent event) {
        this.currentNode = node; // Actualiza el nodo actual
        fileContextMenu.show((Control) event.getSource(), event.getScreenX(), event.getScreenY());    }

    private void renameFolder() {
        TextInputDialog dialog = new TextInputDialog(currentNode.getFolderName());
        dialog.setTitle("Rename File");
        dialog.setHeaderText("Rename File");
        dialog.setContentText("New name:");


        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("renameFolder: " + result.get() + ". path: " + currentNode.getFullPath());
            String newName = result.get();
            fileService.renameFolder(currentNode.getFullPath(), newName);
        }
    }

    private void deleteFolder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete file");
        alert.setHeaderText("¿You sure you wanna delete this file?");
        alert.setContentText("This can not be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("deleteFolder: " + result.get() + ". path: " + currentNode.getFullPath());
            fileService.deleteFolder(currentNode.getFullPath());
        }
    }

    private void showProperties() {
        PropertiesFolderController propertiesFolderController = new PropertiesFolderController(fileService);
        propertiesFolderController.show(currentNode);
    }

    private void uploadFile() {
        UploadController uploadController = new UploadController(fileService);
        uploadController.uploadFile(currentNode);
    }

    private void uploadFolder() {
        CreateFolderController createFolderController = new CreateFolderController(fileService);
        createFolderController.createFolder(currentNode);
    }
}
