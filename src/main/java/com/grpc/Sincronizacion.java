package com.grpc;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class Sincronizacion{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void sincronizar() {
        String target = "10.153.91.133:50052";
        //ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();

        // .usePlaintext()
         //       .build();

        try {
            System.out.println("Sync Client");
            Syncronization client = new Syncronization(channel);

            client.sync();

            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String ping()
    {
        String target = "10.153.91.133:50052";
        //ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        try {
            Syncronization sync = new Syncronization(channel);
            sync.ping();
        } catch (Exception e) {
           System.out.println("Error al hacer ping"+ e.getMessage());
        }

        return "ok";
    }

    
}