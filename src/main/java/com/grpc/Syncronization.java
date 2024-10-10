package com.grpc;

import java.io.FileReader;
import java.io.IOException;

import com.google.api.Advice.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.grpc.util.StreamsManager;
import com.grpc.util.SyncUtil;

import alejandro.model.userSingleton;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import sync.SyncServiceGrpc;
import sync.Sync.Block;
import sync.Sync.Checksum;
import sync.Sync.ChecksumsList;
import sync.Sync.DownloadBlockRequest;
import sync.Sync.DownloadBlockResponse;
import sync.Sync.DownloadRequest;
import sync.Sync.DownloadResponse;
import sync.Sync.FileData;
import sync.Sync.FileList;
import sync.Sync.FileUploadRequest;
import sync.Sync.FileUploadResponse;
import sync.Sync.PingReply;
import sync.Sync.PingRequest;
import sync.Sync.SyncRequest;
import sync.Sync.SyncResponse;
import sync.Sync.UploadBlockRequest;
import sync.Sync.UploadResponse;
import sync.SyncServiceGrpc.SyncServiceBlockingStub;
import sync.SyncServiceGrpc.SyncServiceStub;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


import java.util.Set;
import java.util.Map.Entry;

import java.io.RandomAccessFile;

public class Syncronization {
    private final SyncServiceBlockingStub blockingStub;
    private final SyncServiceStub asyncStub;
    private final SyncUtil util;
    private final String dirPath;
    private final String systemSep;
    private final String username;

    public Syncronization(Channel channel) { 
        blockingStub = SyncServiceGrpc.newBlockingStub(channel);
        asyncStub = SyncServiceGrpc.newStub(channel);
        util = new SyncUtil();
        dirPath = "D:\\LocalFiles";
        systemSep = "\\";
        username = userSingleton.getUsername();
    }

    public void ping() {
        PingRequest request = PingRequest.newBuilder().build();
        PingReply reply;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        /* 
        try (FileReader reader = new FileReader("demo/src/main/java/com/grpc/log.json")) {
            DeletedFilesResponse fileInfo = gson.fromJson(reader, DeletedFilesResponse.class);
            // System.out.println(fileInfo);

            for (com.grpc.File file : fileInfo.getDeleted()) {
                System.out.println(file.getPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reply = blockingStub.ping(request);
            System.out.println(reply);
        } catch (Exception e) {
            System.out.println(e);
        }
            */
    }

    private String leerUsuarioDesdeArchivo(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo: " + e.getMessage(), e);
        }
    }

    public void sync() {
        try {
            String user = leerUsuarioDesdeArchivo("nosoyeltoken.txt");
            
            Map<String, FileData> clientTree = util.getTree(dirPath);
            List<String> elementsToRemove = new ArrayList<>();
            SyncRequest request = SyncRequest
                .newBuilder()
                .setSep(systemSep)
                .setUser(user)
                .putAllClientTree(clientTree)
                .addAllToRemove(elementsToRemove)
                .build();

            SyncResponse response = blockingStub.sync(request);

            DoSync(response.getElementsMap().entrySet());
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuario: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error en la sincronización: " + e.getMessage());
        }
    }

    private void DoSync(Set<Entry<String, FileList>> entries) {
        System.out.println("-> Elements:");

        for (Map.Entry<String, FileList> entry : entries) {
            String key = entry.getKey();
            FileList fileList = entry.getValue();

            if (key.equals("toClientRemove")) {
                for (FileData element : fileList.getElementsList()) {
                    File file = new File(dirPath + systemSep + element.getPath());

                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("El archivo ha sido eliminado exitosamente.");
                        } else {
                            System.out.println("No se pudo eliminar el archivo.");
                        }
                    }
                }
            }

            if (key.equals("toSendToServer")) {
                for (FileData element : fileList.getElementsList()) {
                    upload(dirPath + systemSep + element.getPath(), element.getFolderFingerprint());
                }
            }

            if (key.equals("toClientCreate")) {
                File directory;

                for (FileData element : fileList.getElementsList()) {
                    if (element.getIsDir()) {
                        directory = new File(dirPath + element.getPath());
                        if (!directory.exists()) directory.mkdirs();

                    } else {
                        download(dirPath + systemSep + element.getPath(), element.getFingerprint());
                    }
                }
            }

            if (key.equals("toClientUpdate")){
                for (FileData element : fileList.getElementsList()) {
                    download(dirPath + systemSep + element.getPath(), element.getFingerprint());
                }
            }

            if (key.equals("toServerUpdate")){
                for (FileData element : fileList.getElementsList()) {
                    upload(dirPath + systemSep + element.getPath(), element.getFolderFingerprint()); 
                }
            }
        }
    }

    public void upload(String fullFilepath, String folderFingerprint) {
        StreamsManager<FileUploadResponse> streamManager = new StreamsManager<FileUploadResponse>();
        
        try {
            java.io.File file = new java.io.File(fullFilepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            StreamObserver<FileUploadResponse> responseObserver = streamManager.getStreamObserver("File upload status: ", "File upload failed: ", "File upload completed.");
            StreamObserver<FileUploadRequest> requestObserver = this.asyncStub.upload(responseObserver);

            try {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    FileUploadRequest request = FileUploadRequest.newBuilder()
                            .setChunk(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                            .setFileName(file.getName())
                            .setFolderFingerprint(folderFingerprint)
                            .setUsername(username)
                            .build();

                    requestObserver.onNext(request);
                }   
                requestObserver.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(String fullFilepath, String fingerprint) {
        try {
            FileOutputStream outputStream = new FileOutputStream(fullFilepath);

            DownloadRequest request = DownloadRequest.newBuilder()
                    .setFingerprint(fingerprint)
                    .build();

            StreamObserver<DownloadResponse> responseObserver = new StreamObserver<DownloadResponse>() {
                @Override
                public void onNext(DownloadResponse response) {
                    try {
                        outputStream.write(response.getChunk().toByteArray());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("File upload failed: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    System.out.println("File upload completed.");
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            this.asyncStub.download(request, responseObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
