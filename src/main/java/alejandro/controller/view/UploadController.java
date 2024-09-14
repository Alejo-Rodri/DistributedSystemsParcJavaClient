package alejandro.controller.view;

import alejandro.controller.repository.GrpcClient;
import alejandro.model.FileService;
import alejandro.model.domain.Node;
import alejandro.model.domain.User;
import alejandro.utils.Logs;
import com.google.protobuf.ByteString;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UploadController {
    FileService fileService;

    public UploadController(FileService fileService) {
        this.fileService = fileService;
    }

    public void uploadFile(Node currentNode) {
        if (currentNode == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a folder first.",
                    ButtonType.OK);
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            System.out.println("current node path: " + currentNode.getFullPath());
            if (fileService.uploadFile(file.getPath(), currentNode.getFullPath())) {
                String[] path = currentNode.getFullPath().split("/");
                System.out.println("mierdota");
                currentNode.addFile(new alejandro.model.domain.File(path[path.length - 1], currentNode.getFullPath(), "10:30am"));
            }
        }
    }

    private byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }
}
