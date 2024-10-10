package alejandro.model;

public class FileU {
    private String _name;
    private String _id;
    private String _route;
    private int _size;
    private String _owner;
    private String _permissions;
    private String _modified;
    private String _mime_type;
    private String _path;

    // Getters y setters
    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getRoute() {
        return _route;
    }

    public void setRoute(String route) {
        this._route = route;
    }

    public int getSize() {
        return _size;
    }

    public void setSize(int size) {
        this._size = size;
    }

    public String getOwner() {
        return _owner;
    }

    public void setOwner(String owner) {
        this._owner = owner;
    }

    public String getPermissions() {
        return _permissions;
    }

    public void setPermissions(String permissions) {
        this._permissions = permissions;
    }

    public String getModified() {
        return _modified;
    }

    public void setModified(String modified) {
        this._modified = modified;
    }

    public String getMimeType() {
        return _mime_type;
    }

    public void setMimeType(String mime_type) {
        this._mime_type = mime_type;
    }

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        this._path = path;
    }
}
