package alejandro.controller.repository;

import alejandro.model.domain.User;
import alejandro.utils.Logs;

import com.google.protobuf.ByteString;
import com.grpc.demo.services.AuthServiceGrpc;
import com.grpc.demo.services.Authservice;
import com.grpc.demo.services.Fileserver;
import io.grpc.*;
import com.grpc.demo.services.FilesRouteGrpc;
import com.grpc.demo.services.Fileserver.PingRequest;
import com.grpc.demo.services.Fileserver.PingReply;
import com.grpc.demo.services.Fileserver.RenameFileRequest;
import com.grpc.demo.services.Fileserver.RenameFileResponse;
import com.grpc.demo.services.Fileserver.DownloadRequest;
import com.grpc.demo.services.Fileserver.DownloadResponse;
import com.grpc.demo.services.Fileserver.DeleteFileRequest;
import com.grpc.demo.services.Fileserver.DeleteFileResponse;
import com.grpc.demo.services.Fileserver.FileUploadRequest;
import com.grpc.demo.services.Fileserver.FileUploadResponse;
import com.grpc.demo.services.Fileserver.CreateFolderRequest;
import com.grpc.demo.services.Fileserver.CreateFolderResponse;
import com.grpc.demo.services.Fileserver.GetVersionsRequest;
import com.grpc.demo.services.Fileserver.GetVersionsResponse;
import com.grpc.demo.services.Fileserver.GetAllFilesRequest;
import com.grpc.demo.services.Fileserver.GetAllFilesResponse;
import io.grpc.stub.StreamObserver;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrpcClient {
    private final ManagedChannel channel;
    private final FilesRouteGrpc.FilesRouteBlockingStub blockingStub;
    private final FilesRouteGrpc.FilesRouteStub asyncStub;
    private final AuthServiceGrpc.AuthServiceBlockingStub fileBlockingStub;

    public GrpcClient(String host, String port) {
        /*
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()  // No usar SSL para la conexión
                .build();*/
        System.out.println(host + ":" + port);

        channel = Grpc.newChannelBuilder(host + ":" + port, InsecureChannelCredentials.create()).build();
        blockingStub = FilesRouteGrpc.newBlockingStub(channel);
        asyncStub = FilesRouteGrpc.newStub(channel);
        //fileBlockingStub = AuthServiceGrpc.newStub(channel);
        fileBlockingStub = AuthServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void ping() {
        PingRequest request = PingRequest.newBuilder().build();
        PingReply response;
        try {
            response = blockingStub.ping(request);
            Logs.logINFO(this.getClass(), "Ping response: " + response.getMessage());
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC rename failed", e);
        }
    }

    public boolean renameFile(String jwt, String filePath, String newName) {
        RenameFileRequest request = RenameFileRequest.newBuilder()
                .setUsername(jwt)
                .setFilePath(filePath)
                .setNewName(newName)
                .build();
        RenameFileResponse response;

        try {
            response = blockingStub.renameFile(request);
            Logs.logINFO(this.getClass(), "Rename response: " + response.getMessage());
            return response.getSuccess();
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC rename failed", e);
            return false;
        }
    }

    public void download(String path, String fileName) {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            System.out.println(tempDir);
            FileOutputStream outputStream = new FileOutputStream(tempDir + fileName);
            //FileOutputStream outputStream = new FileOutputStream("C:\\Users\\casec\\Documents\\Projects\\ChatAppWebSockets\\server");

            DownloadRequest request = DownloadRequest.newBuilder()
                    .setPath(path)
                    .build();

            StreamObserver<DownloadResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(DownloadResponse response) {
                    try {
                        System.out.println("File upload status: " + "Write...");
                        System.out.println(response.getChunk());
                        outputStream.write(response.getChunk().toByteArray());
                    } catch (Exception e) {
                        Logs.logWARNING(this.getClass(), "Error obteniendo el chunk", e);
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
                        Logs.logWARNING(this.getClass(), "Error cerrando el outputStream", e);
                    }
                }
            };

            this.asyncStub.download(request, responseObserver);
            System.out.println("Image downloaded successfully!");
        } catch (Exception e) {
            Logs.logWARNING(this.getClass(), "Error descargando en general xd", e);
        }
    }


    public boolean deleteFile(String jwt, String path) {
        DeleteFileRequest request = DeleteFileRequest.newBuilder()
                .setUsername(jwt)
                .setPath(path)
                .build();
        DeleteFileResponse response;

        try {
            response = blockingStub.deleteFile(request);
            Logs.logINFO(this.getClass(), "Rename response: " + response.getSuccess());
            return response.getSuccess();
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC rename failed", e);
            return false;
        }
    }

    public boolean upload(String filePath, String filePathToUpload) {
        try {
            java.io.File file = new java.io.File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            StreamObserver<FileUploadResponse> responseObserver = new StreamObserver<>() {
                @Override
                public void onNext(FileUploadResponse response) {
                    System.out.println("File upload status: " + response.getFileName());
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("File upload failed: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    System.out.println("File upload completed.");
                }
            };

            StreamObserver<FileUploadRequest> requestObserver = this.asyncStub.upload(responseObserver);

            try {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    FileUploadRequest request = FileUploadRequest.newBuilder()
                            .setChunk(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                            .setFileName(file.getName())
                            .setFolderPath(filePathToUpload)
                            .build();
                    requestObserver.onNext(request);
                }

                return true;
            } catch (IOException e) {
                Logs.logWARNING(this.getClass(), "Error haciendo la peticion", e);
                requestObserver.onError(e);
                return false;
            } finally {
                requestObserver.onCompleted();
            }
        } catch (Exception e) {
            Logs.logWARNING(this.getClass(), "Error subiendo el archivo", e);
            return false;
        }
    }

    public boolean createFolder(String jwt, String folderPath, String folderName) {
        CreateFolderRequest request = CreateFolderRequest.newBuilder()
                .setUsername(jwt)
                .setFolderPath(folderPath)
                .setFolderName(folderName)
                .build();

        CreateFolderResponse response;

        try {
            response = blockingStub.createFolder(request);
            Logs.logINFO(this.getClass(), "Create folder response: " + response.getSuccess() + response.getMessage());
            return response.getSuccess();
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC create folder failed", e);
            return false;
        }
    }

    public void register(String uid, String cn, String sn, String mail, String psswd) {
        Authservice.RegisterRequest request = Authservice.RegisterRequest.newBuilder()
                .setUid(uid)
                .setName(cn)
                .setLastname(sn)
                .setEmail(mail)
                .setPassword(psswd)
                .build();

        Authservice.RegisterResponse reply;

        try {
            reply = fileBlockingStub.createUser(request);
            System.out.println(reply);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String authenticate(String username, String password) {
        Authservice.LoginRequest request = Authservice.LoginRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();

        Authservice.LoginResponse reply;

        try {
            reply = fileBlockingStub.login(request);
                System.out.println(reply);
                return reply.getToken();
            } catch (Exception e) {
                System.out.println(e);
                return "no";
            }
    }

        public void addUserToGroup() {
            Authservice.AddUserToGroupRequest request = Authservice.AddUserToGroupRequest.newBuilder()
                    .setUsername("elanticristo")
                    .setGroupname("alejandro123")
                    .build();

            Authservice.AddUserToGroupResponse reply;

            try {
                reply = fileBlockingStub.addUserToGroup(request);
                System.out.println(reply);
            } catch (Exception e) {
                System.out.println(e);
            }
    }

    public boolean modifyPermissions(String jwt, String fullPath, long permissions) {
        Fileserver.ChangePermissionsRequest request = Fileserver.ChangePermissionsRequest.newBuilder()
                .setPermissions(permissions)
                .setFilePath(fullPath)
                .build();

        Fileserver.ChangePermissionsResponse response;
        try {
            response = blockingStub.chmodFile(request);
            System.out.println(response.getSuccess());
            return response.getSuccess();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean addUserToGroup(String newUser, String username) {
        Authservice.AddUserToGroupRequest request = Authservice.AddUserToGroupRequest.newBuilder()
                .setUsername(newUser)
                .setGroupname(username)
                .build();

        Authservice.AddUserToGroupResponse response;
        try {
            response = fileBlockingStub.addUserToGroup(request);
            System.out.println(response.getSuccess());
            return response.getSuccess();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }



    public ArrayList<String> getVersions(String fullPath) {
        GetVersionsRequest request = GetVersionsRequest.newBuilder()
                .setFilePath(fullPath)
                .build();

        return null;
        /*
        try {
            // Llamada al servicio gRPC
            response = blockingStub.get(request).next();

            // Obtener la lista de versiones
            ArrayList<String> versionNames = response.getVersionNameList();

            // Registro de la respuesta obtenida
            Logs.logINFO(this.getClass(), "Get versions response: " + versionNames);

            // Retornar la lista de versiones
            return versionNames;
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC get versions failed", e);
            // Devolver una lista vacía o null en caso de error
            return null;
        }*/
    }

    public boolean restoreVersion() {
        return false;
    }

    public String getSharedFiles(String jwt) {
        Fileserver.GetSharedFilesRequest request = Fileserver.GetSharedFilesRequest.newBuilder()
                .setUsername(jwt)
                .build();

        try {
            Fileserver.GetSharedFilesResponse response = blockingStub.getSharedFiles(request);

            StringBuilder filesList = new StringBuilder();
            for (String file : response.getFilesList()) {
                filesList.append(file).append("\n");
            }

            Logs.logINFO(this.getClass(), "Archivos obtenidos: " + filesList);
            return filesList.toString();
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC get files failed", e);
            return "Error al obtener archivos compartidos";
        }
    }

    public String getRootJson(String jwt, String username) {
        GetAllFilesRequest request = GetAllFilesRequest.newBuilder()
                .setFolderPath(username)
                .build();

        GetAllFilesResponse response;


        try {
            response = blockingStub.getFolderFiles(request);
            Logs.logINFO(this.getClass(), "Get files: " + response.getTree());
            return response.getTree();
        } catch (StatusRuntimeException e) {
            Logs.logWARNING(this.getClass(), "RPC get files failed", e);
            return "false";
        }

        /*
        return """
                {
                  "operation": "something",
                  "root": {
                    "operation": "initialData",
                    "path": "/home/user",
                    "is_dir": true,
                    "mod_time": 1694526845,
                    "children": [
                      {
                        "path": "/home/user/file1.txt",
                        "is_dir": false,
                        "mod_time": 1694526800
                      },
                      {
                        "path": "/home/user/dir1",
                        "is_dir": true,
                        "mod_time": 1694526801,
                        "children": [
                          {
                            "path": "/home/user/dir1/file2.txt",
                            "is_dir": false,
                            "mod_time": 1694526802
                          },
                          {
                            "path": "/home/user/dir1/subdir",
                            "is_dir": true,
                            "mod_time": 1694526803,
                            "children": []
                          }
                        ]
                      },
                      {
                        "path": "/home/user/dir2",
                        "is_dir": true,
                        "mod_time": 1694526804,
                        "children": [
                          {
                            "path": "/home/user/dir2/file3.txt",
                            "is_dir": false,
                            "mod_time": 1694526805
                          }
                        ]
                      }
                    ]
                  }
                }
                """;

         */
    }
}