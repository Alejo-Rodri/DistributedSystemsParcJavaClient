package com.grpc;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.ArrayList;
// import java.util.List;

// import com.grpc.demo.services.DirSyncGrpc;
// import com.grpc.demo.services.DirSyncGrpc.DirSyncBlockingStub;
// import com.grpc.demo.services.DirSyncGrpc.DirSyncStub;
// import com.grpc.demo.services.Syncservice.Element;
// import com.grpc.demo.services.Syncservice.PingReply;
// import com.grpc.demo.services.Syncservice.PingRequest;
// import com.grpc.demo.services.Syncservice.SyncRequest;
// import com.grpc.demo.services.Syncservice.SyncResponse;

// import io.grpc.Channel;

public class SyncClient {
    // private final DirSyncBlockingStub blockingStub;
    // private final DirSyncStub asyncStub;

    // public SyncClient(Channel channel) {
    //     blockingStub = DirSyncGrpc.newBlockingStub(channel);
    //     asyncStub = DirSyncGrpc.newStub(channel);
    // }

    // public void ping() {
    //     PingRequest request = PingRequest.newBuilder().build();

    //     PingReply reply;

    //     try {
    //         reply = blockingStub.ping(request);
    //         System.out.println(reply);
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    // }

    // private static SyncRequest buildSyncRequest(String dirPath) {
    //     SyncRequest.Builder requestBuilder = SyncRequest.newBuilder();
    //     try {
    //         Path baseDir = Paths.get("/home/gianmarco/Desktop/distribuidos/clientsNFS/fernanda");
    
    //         requestBuilder.setSep("/").setClientPath("/fernanda");

    //         List<Element> elements = new ArrayList<>();
    //         File root = new File(dirPath);

    //         for (File file : root.listFiles()) {
    //             if (file.isDirectory()) {
    //                 for (File current : file.listFiles()) {
    //                     System.out.println("element " + current.getName());
    //                     Element element = Element.newBuilder()
    //                             .setPath(baseDir.relativize(current.toPath()).toString())
    //                             .setIsDir(current.isDirectory())
    //                             .setModTime(current.lastModified()).build();
    //                     elements.add(element);
    //                 }
    //             } 
    //                 Element element = Element.newBuilder()
    //                 .setPath(baseDir.relativize(file.toPath()).toString())
    //                 .setIsDir(file.isDirectory())
    //                 .setModTime(file.lastModified()).build();
    //                 elements.add(element);

    //         }

    //         System.out.println("elements");
    //         for (Element element : elements){
    //             System.out.println(element.getPath());
    //         }



    //         requestBuilder.addAllElements(elements);
    //     } catch (Exception e) {
    //         // TODO: handle exception
    //     }
    //     return requestBuilder.build();

    // }

    // public static String calculateSHA256Checksum(String filePath) throws NoSuchAlgorithmException, IOException {
    //     MessageDigest digest = MessageDigest.getInstance("SHA-256");
    //     try (FileInputStream fis = new FileInputStream(filePath)) {
    //         byte[] buffer = new byte[1024];
    //         int bytesRead;
    //         while ((bytesRead = fis.read(buffer)) != -1) {
    //             digest.update(buffer, 0, bytesRead);
    //         }
    //     }
    //     byte[] checksumBytes = digest.digest();
    //     StringBuilder sb = new StringBuilder();
    //     for (byte b : checksumBytes) {
    //         sb.append(String.format("%02x", b));
    //     }
    //     return sb.toString();
    // }

    // public static long calculateAdler32Checksum(String filePath) throws IOException {
    //     java.util.zip.Adler32 adler32 = new java.util.zip.Adler32();
    //     try (FileInputStream fis = new FileInputStream(filePath)) {
    //         byte[] buffer = new byte[1024];
    //         int bytesRead;
    //         while ((bytesRead = fis.read(buffer)) != -1) {
    //             adler32.update(buffer, 0, bytesRead);
    //         }
    //     }
    //     return adler32.getValue();
    // }

    // public void sync() {
    //     SyncRequest request = buildSyncRequest("/home/gianmarco/Desktop/distribuidos/clientsNFS/fernanda");
    //     SyncResponse reply;

    //     try {
    //         reply = blockingStub.syncStructure(request);
    //         System.out.println(reply);
    //     } catch (Exception e) {
    //         System.out.println(e);
    //     }
    // }
}