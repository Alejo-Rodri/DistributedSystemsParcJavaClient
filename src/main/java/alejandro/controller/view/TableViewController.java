package alejandro.controller.view;

import alejandro.controller.MainController;
import alejandro.model.domain.File;
import alejandro.model.domain.Node;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

public class TableViewController {
    @FXML
    private TableColumn<Object, String> nameColumn;
    private final TableView<Object> contentTableView;
    private ContextMenuFileController contextMenuFileController;

    public TableViewController(TableView<Object> contentTableView, TableColumn<Object, String> nameColumn, ContextMenuFileController contextMenuFileController) {
        this.contentTableView = contentTableView;
        this.nameColumn = nameColumn;
        this.contextMenuFileController = contextMenuFileController;
        setupTableView();
    }

    private void setupTableView() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        contentTableView.setRowFactory(tableView -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    //fileContextMenu.show(row, event.getScreenX(), event.getScreenY());
                    contextMenuFileController.show(row, event);
                }
            });
            return row;
        });
    }

    public void showContent(Node node) {
        contextMenuFileController.setCurrentNode(node);
        contentTableView.getItems().clear();
        for (Node child : node.getChildren())
            contentTableView.getItems().add(new MainController.FileFolderItem(child.getFolderName(), "Carpeta"));

        for (File file : node.getFiles())
            contentTableView.getItems().add(new MainController.FileFolderItem(file.getFileName(), "Archivo"));
    }

    public void showSearchResults(ObservableList<File> searchResults) {
        contentTableView.getItems().clear();
        for (File file : searchResults) {
            contentTableView.getItems().add(new MainController.FileFolderItem(file.getFileName(), "Archivo"));
        }
    }
}
