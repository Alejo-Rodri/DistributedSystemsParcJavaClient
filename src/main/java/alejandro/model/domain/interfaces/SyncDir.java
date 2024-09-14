package alejandro.model.domain.interfaces;

public class SyncDir {
    public String operation;
    public JsonNode root;

    public String getOperation() {
        return operation;
    }

    public JsonNode getRoot() {
        return root;
    }
}
