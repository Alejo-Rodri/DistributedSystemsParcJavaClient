package com.grpc;
import javax.print.DocFlavor.STRING;

public class File {

    private String date;
    private String fingerprint;
    private String path;


    public String getDate() {
        return date;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                
                "modified=" + date +
                ", fingerprint='" + fingerprint + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
