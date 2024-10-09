package com.grpc;

import java.util.List;

public class DeletedFilesResponse {
    private List<File> deleted;

    // Getter y Setter
    public List<File> getDeleted() {
        return deleted;
    }

    public void setDeleted(List<File> deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "DeletedFilesResponse{" +
                "deleted=" + deleted +
                '}';
    }
}