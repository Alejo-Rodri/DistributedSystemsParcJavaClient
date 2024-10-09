package com.grpc.util;
import sync.Sync.FileData;
import java.util.Map;
import java.util.HashMap;


import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.*;

import java.io.IOException;

import java.util.Arrays;

import java.security.MessageDigest;


public class SyncUtil {
    
    public Map<String, FileData> getTree (String userFolderpath) {
        Map<String, FileData> clientTree = new HashMap<>();
        
        try {
            Path dirPath = Paths.get(userFolderpath);
    
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (!dirPath.relativize(dir).toString().equals("")) {
                            clientTree.put(
                                    dirPath.relativize(dir) + "",
                                    FileData.newBuilder().setPath(dirPath.relativize(dir) + "")
                                            .setModified(attrs.lastAccessTime() + "").setIsDir(attrs.isDirectory())
                                            .build());
                        }
                        return FileVisitResult.CONTINUE;
                    }
    
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        clientTree.put(
                                dirPath.relativize(file) + "",
                                FileData.newBuilder()
                                        .setPath(dirPath.relativize(file) + "")
                                        .setModified(attrs.lastAccessTime() + "")
                                        .setIsDir(attrs.isDirectory())
                                        .setSize(attrs.size())
                                        .build());
                        return FileVisitResult.CONTINUE;
                    }
                });
                return clientTree;
        } catch (Exception e) {
            return clientTree;
        }
    }

    public String getStrong(byte[] bytes, int length) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(Arrays.copyOf(bytes, length));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
