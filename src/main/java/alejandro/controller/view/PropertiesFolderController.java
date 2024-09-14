package alejandro.controller.view;

import alejandro.model.FileService;
import alejandro.model.domain.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PropertiesFolderController {
    private final FileService fileService;

    public PropertiesFolderController(FileService fileService) {
        this.fileService = fileService;
    }

    public void show(Node currentNode) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Propiedades del archivo");
        dialog.setHeaderText("Propiedades de " + currentNode.getFolderName());

        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);

        ComboBox<String> groupPermissionBox = new ComboBox<>();
        groupPermissionBox.getItems().addAll("Ninguno", "Lectura", "Escritura");
        groupPermissionBox.setValue("Ninguno");

        ComboBox<String> othersPermissionBox = new ComboBox<>();
        othersPermissionBox.getItems().addAll("Ninguno", "Lectura", "Escritura");
        othersPermissionBox.setValue("Ninguno");

        TextField addUserField = new TextField();
        addUserField.setPromptText("Agregar usuarios al grupo");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Ruta completa:"), 0, 0);
        grid.add(new Label(currentNode.getFullPath()), 1, 0);

        grid.add(new Label("Permisos del grupo:"), 0, 1);
        grid.add(groupPermissionBox, 1, 1);

        grid.add(new Label("Permisos de otros:"), 0, 2);
        grid.add(othersPermissionBox, 1, 2);

        grid.add(new Label("Agregar usuarios al grupo:"), 0, 3);
        grid.add(addUserField, 1, 3);

        Button modifyPermissionsButton = getButton(currentNode, groupPermissionBox, othersPermissionBox);

        Button addUserButton = new Button("Añadir usuario");
        addUserButton.setOnAction(event -> {
            String newUser = addUserField.getText();
            if (!newUser.isEmpty()) {
                addUserToGroup(currentNode, newUser);
                addUserField.clear();
            }
        });

        grid.add(modifyPermissionsButton, 1, 4);
        grid.add(addUserButton, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }

    private Button getButton(Node currentNode, ComboBox<String> groupPermissionBox, ComboBox<String> othersPermissionBox) {
        Button modifyPermissionsButton = new Button("Modificar permisos");
        modifyPermissionsButton.setOnAction(event -> {
            int groupPermissionOctal = convertPermissionToOctal(groupPermissionBox.getValue());
            int othersPermissionOctal = convertPermissionToOctal(othersPermissionBox.getValue());

            int permissionValue = 0700 + groupPermissionOctal * 10 + othersPermissionOctal;
            modifyPermissions(currentNode, permissionValue);
        });
        return modifyPermissionsButton;
    }

    private int convertPermissionToOctal(String permission) {
        return switch (permission) {
            case "Lectura" -> 4;
            case "Escritura" -> 6;
            default -> 0;
        };
    }

    private void modifyPermissions(Node node, int permissionVal) {
        System.out.println("Modificando permisos para: " + node.getFullPath());
        System.out.println("Permisos (octal): " + permissionVal);


        fileService.modifyPermissions(node.getFullPath(), permissionVal);
    }

    private void addUserToGroup(Node node, String newUser) {
        System.out.println("Añadiendo usuario al grupo: " + newUser + " para el nodo: " + node.getFullPath());

        fileService.addUserToGroup(newUser, fileService.getUsername());
    }
}
