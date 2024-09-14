package alejandro.controller.repository;

import alejandro.model.Services;
import alejandro.utils.Environment;
import alejandro.utils.Logs;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class GoSocket extends Thread {
    private BufferedReader reader;
    private PrintWriter out;

    Services services;

    /*
    Thread readThread = new Thread(() -> {
    try {
    String message;

    readThread.start();

    // System.out.println("...");
    // String userInput;
    while (!(userInput = scanner.nextLine()).equalsIgnoreCase("exit")) {
    out.println(userInput);
    }

    // System.out.println("saliendodooo");

    } catch (Exception e) {
    e.printStackTrace();
    }

     */

    public GoSocket(Services services) {
        try {
            Socket socket = new Socket(Environment.getInstance().getVariables().get("GO_IP"),
                    Integer.parseInt(Environment.getInstance().getVariables().get("GO_PORT")));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.services = services;
        } catch (IOException e) {
            Logs.logWARNING(this.getClass(), "Error in GoSocket class", e);
            reader = null;
            services = null;
        }
    }

    public void setUsername(String username) {
        out.println(username);
    }

    @Override
    public void run() {
        try {
            System.out.println("something");
            String jsonString;
        while ((jsonString = reader.readLine()) != null) {
            System.out.println("Received: " + jsonString);
            services.handleSocket(jsonString);
        }
        } catch (Exception e) {
            Logs.logWARNING(this.getClass(), "Reader is null, probably", e);
        }
    }

}
