package alejandro.controller.view;

import alejandro.controller.repository.GrpcClient;
import alejandro.model.FileService;
import alejandro.model.domain.Node;
import alejandro.model.domain.User;
import alejandro.utils.Logs;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class CreateFolderController {
    FileService fileService;

    public CreateFolderController(FileService fileService) {
        this.fileService = fileService;
    }

    public void createFolder(Node currentNode) {
        if (currentNode == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Por favor, selecciona una carpeta primero.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Crear carpeta");
        dialog.setHeaderText("Introduce el nombre de la nueva carpeta:");

        ButtonType createButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        TextField folderNameField = new TextField();
        folderNameField.setPromptText("Nombre de la carpeta");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre de la carpeta:"), 0, 0);
        grid.add(folderNameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return folderNameField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(folderName -> {
            if (!folderName.trim().isEmpty()) {
                createFolder(currentNode.getFullPath(), folderName, currentNode);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "El nombre de la carpeta no puede estar vacío.",
                        ButtonType.OK);
                alert.showAndWait();

            }
        });
    }

    private void createFolder(String fullPath, String folderName, Node currentNode) {
        System.out.println("Carpeta: " + folderName);

        try {
            if (fileService.createFolder(fullPath, folderName)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Folder created:).",
                        ButtonType.OK);
                alert.showAndWait();
                currentNode.addChild(new Node(folderName));
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Could not create the folder :c.",
                        ButtonType.OK);
                alert.showAndWait();
            }
        } catch (Exception e) {
            Logs.logWARNING(this.getClass(), "Error while creating the folder", e);
        }
    }
}
