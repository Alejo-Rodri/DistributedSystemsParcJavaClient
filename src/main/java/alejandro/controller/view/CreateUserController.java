package alejandro.controller.view;

import alejandro.model.FileService;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class CreateUserController {
    FileService fileService;

    public CreateUserController(FileService fileService) {
        this.fileService = fileService;
    }

    public void createUser() {
        Dialog<Pair<String, String[]>> dialog = new Dialog<>();
        dialog.setTitle("Crear Usuario");

        ButtonType createUserButtonType = new ButtonType("Crear", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createUserButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField uidField = new TextField();
        uidField.setPromptText("UID");
        TextField cnField = new TextField();
        cnField.setPromptText("Nombre completo");
        TextField snField = new TextField();
        snField.setPromptText("Apellido");
        TextField mailField = new TextField();
        mailField.setPromptText("Correo electrónico");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        grid.add(new Label("UID:"), 0, 0);
        grid.add(uidField, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(cnField, 1, 1);
        grid.add(new Label("Apellido:"), 0, 2);
        grid.add(snField, 1, 2);
        grid.add(new Label("Correo:"), 0, 3);
        grid.add(mailField, 1, 3);
        grid.add(new Label("Contraseña:"), 0, 4);
        grid.add(passwordField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createUserButtonType) {
                return new Pair<>(uidField.getText(), new String[]{
                        cnField.getText(), snField.getText(), mailField.getText(), passwordField.getText()
                });
            }
            return null;
        });

        dialog.showAndWait().ifPresent(userData -> {
            String uid = userData.getKey();
            String[] userDetails = userData.getValue();
            System.out.println("UID: " + uid);
            System.out.println("Nombre: " + userDetails[0]);
            System.out.println("Apellido: " + userDetails[1]);
            System.out.println("Correo: " + userDetails[2]);
            System.out.println("Contraseña: " + userDetails[3]);

            fileService.createUser(uid, userDetails[0], userDetails[1], userDetails[2], userDetails[3]);
        });
    }
}
