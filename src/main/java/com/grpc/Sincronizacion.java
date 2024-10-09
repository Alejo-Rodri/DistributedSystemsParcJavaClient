package com.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class Sincronizacion{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void sincronizar() {
        String target = "10.154.12.122:50052";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
        // .usePlaintext()
                .build();

        try {
            System.out.println("Sync Client");
            Syncronization client = new Syncronization(channel);

            client.sync();

            
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}