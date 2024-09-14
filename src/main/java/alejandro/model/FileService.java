package alejandro.model;

import alejandro.controller.repository.GrpcClient;

import java.util.ArrayList;

public class FileService {
    private final GrpcClient grpcClient;
    private String jwt;
    private String username;

    public FileService(GrpcClient grpcClient, String jwt, String username) {
        this.grpcClient = grpcClient;
        this.jwt = jwt;
        this.username = username;
    }

    public void ping() {
        grpcClient.ping();
    }

    public boolean downloadFile(String path) {
        try {
            String fileName = "file.txt";
            grpcClient.download(path, fileName);
            return true;
            //return grpcClient.downloadFile(jwt, path);
        } catch (Exception e) {
            System.out.println("Error al descargar el archivo: " + e.getMessage());
            return false;
        }
    }

    public void renameFile(String path, String newName) {
        try {
            grpcClient.renameFile(jwt, path, newName);
        } catch (Exception e) {
            System.out.println("Error al renombrar el archivo: " + e.getMessage());
        }
    }

    public void deleteFile(String path) {
        try {
            grpcClient.deleteFile(jwt, path);
        } catch (Exception e) {
            System.out.println("Error al eliminar el archivo: " + e.getMessage());
        }
    }

    public boolean uploadFile(String path, String pathToUpload) {
        //grpcClient.uploadFile(jwt, fileName, folderPath, chunk);
        System.out.println(path);
        return grpcClient.upload(path, pathToUpload);
    }

    public boolean createFolder(String folderPath, String folderName) {
        return grpcClient.createFolder(jwt, folderPath, folderName);
    }

    public void renameFolder(String path, String newName) {
        grpcClient.renameFile(jwt, path, newName);
    }

    public void deleteFolder(String path) {
        grpcClient.deleteFile(jwt, path);
    }

    public void modifyPermissions(String path, long permissionVal) {
        grpcClient.modifyPermissions(jwt, path, permissionVal);
    }

    public void addUserToGroup(String newUser, String username) {
        grpcClient.addUserToGroup(newUser, username);
    }

    public boolean restoreFile(String path) {
       return grpcClient.restoreVersion();
    }

    public ArrayList<String> getVersions(String path) {
        //return grpcClient.getVersions(path);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("a");
        arrayList.add("b");
        arrayList.add("c");
        return arrayList;
    }
    /*
    public List<File> listFiles(String folderPath) {
        try {
            return grpcClient.listFiles(jwt, folderPath);
        } catch (Exception e) {
            System.out.println("Error al listar los archivos: " + e.getMessage());
            return new ArrayList<>();
        }

    }*/

    public void createUser(String uid, String cn, String surname, String mail, String psswd) {
        grpcClient.register(uid, cn, surname, mail, psswd);
    }

    public String getUsername() {
        return username;
    }
}
