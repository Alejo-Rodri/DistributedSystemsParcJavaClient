package alejandro.model.domain;

import java.time.LocalDateTime;

public class File {
    private String fileName;
    private String filePath;
    private float size;
    private String dateCreated;
    private String dateModified;
    private String owner;

    public File(String fileName, String filePath, String dateModified) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.dateModified = dateModified;
        dateCreated = "";
        owner = "";
    }

    public File(String fileName, String filePath, float size, String dateCreated, String owner) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.size = size;
        this.dateCreated = dateCreated;
        dateModified = dateCreated;
        this.owner = owner;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        updateDateModified();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        updateDateModified();
    }

    public void setSize(float size) {
        this.size = size;
        updateDateModified();
    }

    public void updateDateModified() {
        this.dateModified = String.valueOf(LocalDateTime.now());
    }

    public String getFileName() {
        return fileName;
    }

    public String getFullName() {
        return filePath + fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public float getSize() {
        return size;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getOwner() {
        return owner;
    }
}
