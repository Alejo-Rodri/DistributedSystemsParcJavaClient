package com.grpc;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;


public class Sincronizacion{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private ScheduledExecutorService scheduler;
    public Sincronizacion() {
         scheduler = Executors.newScheduledThreadPool(1);
        scheduleDailySync();
    }

    public static void sincronizar() {

        String target = "10.154.12.122:50052";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
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
        String target = "10.154.12.122:50052";
        ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        try {
            Syncronization sync = new Syncronization(channel);
            sync.ping();
        } catch (Exception e) {
           System.out.println("Error al hacer ping"+ e.getMessage());
        }

        return "ok";
    }


    public void scheduleDailySync() {
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
       
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
        long period = TimeUnit.DAYS.toMillis(1);

      
        scheduler.scheduleAtFixedRate(() -> {
            sincronizar();
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    
}