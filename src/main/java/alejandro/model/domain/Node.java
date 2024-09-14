package alejandro.model.domain;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Node {
    private final String folderName;
    private Node root;
    private ObservableList<Node> children;
    private ObservableList<File> files;

    public Node(String folderName) {
        this.folderName = folderName;
        root = null;

        children = FXCollections.observableArrayList();
        files = FXCollections.observableArrayList();
    }

    public void addChild(Node child) {
        child.root = this;
        children.add(child);
    }

    public void addFile(File file) {
        files.add(file);
    }

    public void removeChild(Node child) {
        children.remove(child);
    }

    public void removeFile(File file) {
        files.remove(file);
    }

    public String getFullPath() {
        if (root == null) return folderName;
        else return root.getFullPath() + "/" + folderName;
    }

    public Node searchFolder(String folderName) {
        if (this.folderName.equals(folderName)) return this;

        for (Node child : children) {
            Node result = child.searchFolder(folderName);
            if (result != null) return result;
        }

        return null;
    }

    public void printTree(String indent) {
        System.out.println(indent + "- " + folderName);
        for (File file : files) {
            System.out.println(indent + "  " + file);
        }
        for (Node child : children) {
            child.printTree(indent + "  ");
        }
    }

    public String getFolderName() {
        return folderName;
    }

    public Node getRoot() {
        return root;
    }

    public ObservableList<Node> getChildren() {
        return children;
    }

    public ObservableList<File> getFiles() {
        return files;
    }
}
