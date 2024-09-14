package alejandro.controller.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import alejandro.model.domain.File;
import alejandro.model.domain.Node;

public class SearchController {
    public ObservableList<File> search(Node root, String keyword) {
        ObservableList<File> matchingFiles = FXCollections.observableArrayList();
        searchRecursive(root, keyword, matchingFiles);
        return matchingFiles;
    }

    private void searchRecursive(Node node, String keyword, ObservableList<File> matchingFiles) {
        for (File file : node.getFiles()) {
            if (matchesKeyword(file, keyword)) {
                matchingFiles.add(file);
            }
        }

        for (Node child : node.getChildren()) {
            searchRecursive(child, keyword, matchingFiles);
        }
    }

    private boolean matchesKeyword(File file, String keyword) {
        String lowerKeyword = keyword.toLowerCase();

        return file.getFileName().toLowerCase().contains(lowerKeyword)
                || file.getFileName().toLowerCase().endsWith(lowerKeyword)
                || file.getFullName().toLowerCase().contains(lowerKeyword)
                || file.getDateCreated().toLowerCase().contains(lowerKeyword)
                || file.getDateModified().toLowerCase().contains(lowerKeyword)
                || file.getOwner().toLowerCase().contains(lowerKeyword);
    }
}
