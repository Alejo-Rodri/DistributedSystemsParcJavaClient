package alejandro.model.domain.interfaces;

import java.util.List;

public class JsonNode {
    public String name;
    public String path;
    public boolean is_dir;
    public int size;
    public String mod_time;
    public List<JsonNode> children; // Solo para directorios

    public String getPath() {
        return path;
    }

    public boolean isIs_dir() {
        return is_dir;
    }

    public String getMod_time() {
        return mod_time;
    }

    public List<JsonNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "JsonNode{" +
                "path='" + path + '\'' +
                ", isDir=" + is_dir +
                ", modTime=" + mod_time +
                ", children=" + children +
                '}';
    }
}