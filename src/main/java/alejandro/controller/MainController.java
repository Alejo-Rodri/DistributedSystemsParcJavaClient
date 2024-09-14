package alejandro.controller;

import alejandro.controller.repository.GrpcClient;
import alejandro.controller.view.*;
import alejandro.model.FileService;
import alejandro.model.Services;
import alejandro.model.domain.File;
import alejandro.model.domain.Node;
import alejandro.model.domain.User;
import alejandro.utils.Environment;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Label folderLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TreeView<String> folderTreeView;

    @FXML
    private TableView<Object> contentTableView;

    @FXML
    private TableColumn<Object, String> nameColumn;

    private GrpcClient grpcClient;

    private static User user;

    private Services services;

    private TreeViewController treeViewController;
    private TableViewController tableViewController;
    private ContextMenuFileController contextMenuFileController;
    private ContextMenuFolderController contextMenuFolderController;
    private FileService fileService;

    // TODO hacer que el current node en el inicio sea el rootNode

    public void setServices(Services services) {
        this.services = services;
        services.setTreeViewController(treeViewController);

        services.parseToTree(grpcClient.getRootJson(user.getJwt(), user.getUsername()));
        setRootNode(services.getRootNode());

        String[] split = grpcClient.getSharedFiles(user.getJwt()).split("\n");
        for (String json : split) {
            services.handleSocket(json);
        }


        /*
        services.handleSocket(grpcClient.getSharedFiles(user.getJwt()));
        setRootNode(services.getRootNode());
         */

    }

    public void initialize() {
        usernameLabel.setText("User: " + getUser().getUsername());
        this.grpcClient = new GrpcClient(Environment.getInstance().getVariables().get("GRPC_IP"),
                Environment.getInstance().getVariables().get("GRPC_PORT"));
        fileService = new FileService(grpcClient, user.getJwt(), user.getUsername());

        contextMenuFileController = new ContextMenuFileController(fileService);
        contextMenuFolderController = new ContextMenuFolderController(fileService);

        tableViewController = new TableViewController(contentTableView, nameColumn, contextMenuFileController);
        treeViewController = new TreeViewController(folderTreeView, tableViewController, folderLabel, contextMenuFolderController);
    }

    public void setRootNode(Node rootNode) {
        treeViewController.setRootNode(rootNode);
        tableViewController.showContent(rootNode);
    }

    public static class FileFolderItem {
        private final String name;
        private final String type;

        public FileFolderItem(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    @FXML
    private void onCreateUser() {
        CreateUserController createUserController = new CreateUserController(fileService);
        createUserController.createUser();
    }

    @FXML
    private void onSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            SearchController searchController = new SearchController();
            ObservableList<File> searchResults = searchController.search(services.getRootNode(), query);
            tableViewController.showSearchResults(searchResults);
        }
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainController.user = user;
    }
}
