package alejandro.controller.view;

import alejandro.model.Services;
import alejandro.model.domain.Node;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

public class TreeViewController {
    @FXML
    private Label folderLabel;
    private final TreeView<String> folderTreeView;
    private final TableViewController tableViewController;
    private Node rootNode;
    private Node currentNode;
    private ContextMenuFolderController contextMenuFolderController;
    private Services services;

    public TreeViewController(TreeView<String> folderTreeView, TableViewController tableViewController,
                              Label folderLabel, ContextMenuFolderController contextMenuFolderController) {
        this.folderTreeView = folderTreeView;
        this.tableViewController = tableViewController;
        this.folderLabel = folderLabel;
        this.contextMenuFolderController = contextMenuFolderController;
    }

    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
        setupFolderTreeView();
    }

    private void setupFolderTreeView() {
        TreeItem<String> rootItem = new TreeItem<>(rootNode.getFolderName());
        rootItem.setExpanded(true);
        buildTree(rootNode, rootItem);
        folderTreeView.setRoot(rootItem);

        folderTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Node selectedNode = rootNode.searchFolder(newValue.getValue());
                if (selectedNode != null) {
                    currentNode = selectedNode;
                    folderLabel.setText(currentNode.getFolderName());
                    tableViewController.showContent(currentNode);

                    currentNode.getChildren().addListener((ListChangeListener<Node>) change -> {
                        while (change.next()) {
                            if (change.wasAdded() || change.wasRemoved()) {
                                refreshFolderTreeView();
                                tableViewController.showContent(currentNode);
                            }
                        }
                    } );
                }
            }
        });

        folderTreeView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                TreeItem<String> selectedItem = folderTreeView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    Node selectedNode = rootNode.searchFolder(selectedItem.getValue());
                    if (selectedNode != null) {
                        currentNode = selectedNode;
                        contextMenuFolderController.show(currentNode, selectedItem, event);
                    }
                }
            }
        });
    }

    private void buildTree(Node node, TreeItem<String> treeItem) {
        for (Node child : node.getChildren()) {
            TreeItem<String> childItem = new TreeItem<>(child.getFolderName());
            treeItem.getChildren().add(childItem);
            treeItem.setExpanded(true);
            buildTree(child, childItem);
        }
    }

    public void refreshFolderTreeView() {
        //contextMenuFolderController.setCurrentNode(rootNode);
        TreeItem<String> rootItem = new TreeItem<>(rootNode.getFolderName());
        rootItem.setExpanded(true);
        buildTree(rootNode, rootItem);
        folderTreeView.setRoot(rootItem);
    }

    public Node getCurrentNode() { return currentNode; }


}
