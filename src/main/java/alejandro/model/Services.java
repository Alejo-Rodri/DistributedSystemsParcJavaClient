package alejandro.model;

import alejandro.controller.view.TreeViewController;
import alejandro.model.domain.interfaces.JsonNode;
import alejandro.model.domain.Node;

import alejandro.model.domain.File;
import alejandro.model.domain.interfaces.Socket;
import alejandro.model.domain.interfaces.SyncDir;
import alejandro.utils.Logs;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.source.tree.Tree;
import javafx.scene.control.TreeItem;

public class Services {
    private Node rootNode;
    TreeItem<String> folderTreeView;
    TreeViewController treeViewController;

    public Services() {
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void parseToTree(String json) {
        Gson gson = new Gson();

        try {
            SyncDir rootSyncDir = gson.fromJson(json, SyncDir.class);
            System.out.println(rootSyncDir);

            if (rootSyncDir.getRoot() != null) rootNode = buildNode(rootSyncDir.getRoot(), true);
            else System.out.println("no se pudo");
        } catch (JsonSyntaxException e) {
            Logs.logWARNING(this.getClass(), "Error parsing the json", e);
        }
    }

    private Node buildNode(JsonNode jsonNode, boolean isRoot) {
        String name;

        if (isRoot) name = jsonNode.getPath();
        else {
            String[] pathComponents = jsonNode.getPath().split("/");
            name = pathComponents[pathComponents.length - 1];
        }

        if (jsonNode.isIs_dir()) {
            Node currentNode = new Node(name);

            if (jsonNode.getChildren() != null) {
                for (JsonNode child : jsonNode.getChildren()) {
                    currentNode.addChild(buildNode(child, false));
                }
            }

            return currentNode;
        } else {
            File file = new File(name, jsonNode.getPath(), String.valueOf(jsonNode.getMod_time()));
            Node fileNode = new Node(name);
            fileNode.addFile(file);
            return fileNode;
        }
    }

    public void handleSocket(String json) {
        Gson gson = new Gson();

        try {
            System.out.println("oea" + json);
            System.out.println("otra");
            Socket rootSocket = gson.fromJson(json, Socket.class);
            System.out.println(rootSocket);

            if (rootSocket.getRoot() != null) {
                if (rootSocket.getOperation().equals("newFileAdded")) loadSharedFile(rootSocket.getRoot());
                else if (rootSocket.getOperation().equals("removeSharedFile")) removeSharedFile(rootSocket.getRoot());
            } else System.out.println("no se pudo");
        } catch (JsonSyntaxException e) {
            Logs.logWARNING(this.getClass(), "Error parsing the json", e);
        }
    }

    private void loadSharedFile(String path) {
        String[] pathComponents = path.split("/");
        Node currentNode = rootNode;
        System.out.println("entro aca");

        for (int i = 1; i < pathComponents.length; i++) {
            String component = pathComponents[i];
            System.out.println("sigo por aca");

            if (i == pathComponents.length - 1) {
                File newFile = new File(component, path, String.valueOf(System.currentTimeMillis()));
                currentNode.addFile(newFile);
                System.out.println("Archivo creado: " + newFile.getFileName());
            } else {
                Node child = currentNode.searchFolder(component);
                if (child == null) {
                    child = new Node(component);
                    currentNode.addChild(child);
                    System.out.println("Carpeta creada: " + child.getFolderName());
                }
                currentNode = child;
            }
        }
        treeViewController.refreshFolderTreeView();

        /*
        TreeItem<String> parentItem = folderTreeView.getSelectionModel().getSelectedItem();
        TreeItem<String> newItem = new TreeItem<>(newFile.getFileName());
        parentItem.getChildren().add(newItem);


         */
    }

    private void removeSharedFile(String path) {
        if (rootNode == null) {
            System.out.println("No se ha cargado el árbol de directorios.");
            return;
        }

        String[] pathComponents = path.split("/");

        Node currentNode = rootNode;
        for (int i = 1; i < pathComponents.length; i++) {
            String component = pathComponents[i];

            if (i == pathComponents.length - 1) {
                File fileToRemove = null;
                for (File file : currentNode.getFiles()) {
                    if (file.getFileName().equals(component)) {
                        fileToRemove = file;
                        break;
                    }
                }

                if (fileToRemove != null) {
                    currentNode.removeFile(fileToRemove);
                    System.out.println("Archivo eliminado: " + fileToRemove.getFileName());
                } else {
                    Node folderToRemove = currentNode.searchFolder(component);
                    if (folderToRemove != null) {
                        currentNode.removeChild(folderToRemove);
                        System.out.println("Carpeta eliminada: " + folderToRemove.getFolderName());
                    } else {
                        System.out.println("Archivo o carpeta no encontrados.");
                    }
                }
            } else {
                Node child = currentNode.searchFolder(component);
                if (child == null) {
                    System.out.println("Carpeta no encontrada: " + component);
                    return;
                }
                currentNode = child;
            }
        }

        if (currentNode.getFiles().isEmpty() && currentNode.getChildren().isEmpty()) {
            Node parent = currentNode.getRoot();
            if (parent != null) {
                parent.removeChild(currentNode);
                System.out.println("Carpeta vacía eliminada: " + currentNode.getFolderName());
            }
        }
    }

    public void setFolderTreeView(TreeItem<String> folderTreeView) {
        this.folderTreeView = folderTreeView;
    }

    public void setTreeViewController(TreeViewController treeViewController) {
        this.treeViewController = treeViewController;
    }
}
