package alejandro.controller.view;

import alejandro.model.FileService;
import alejandro.model.domain.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

public class PropertiesFileController {
    private final FileService fileService;

    public PropertiesFileController(FileService fileService) {
        this.fileService = fileService;
    }

    public void show(Node currentNode) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Propiedades del archivo");
        dialog.setHeaderText("Versiones disponibles para " + currentNode.getFolderName());

        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        List<String> versions = fileService.getVersions(currentNode.getFullPath());

        // ComboBox para mostrar las versiones disponibles
        ComboBox<String> versionsBox = new ComboBox<>();
        versionsBox.getItems().addAll(versions);

        Button restoreButton = getButton(currentNode, versionsBox);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Versiones disponibles:"), 0, 0);
        grid.add(versionsBox, 1, 0);
        grid.add(restoreButton, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private Button getButton(Node currentNode, ComboBox<String> versionsBox) {
        Button restoreButton = new Button("Restaurar");
        restoreButton.setOnAction(event -> {
            String selectedVersion = versionsBox.getValue();
            if (selectedVersion != null) {
                fileService.restoreFile(currentNode.getFullPath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Restauración completada");
                alert.setHeaderText(null);
                alert.setContentText("El archivo ha sido restaurado a la versión: " + selectedVersion);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error de selección");
                alert.setHeaderText(null);
                alert.setContentText("Por favor, seleccione una versión.");
                alert.showAndWait();
            }
        });
        return restoreButton;
    }
}
