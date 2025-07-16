package ru.geekbrains.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // region Fields
    private final ServerSocket serverSocket;
    // endregion

    // region Constructors
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    // endregion

    // region Methods
    public void runServer(){
        while (!serverSocket.isClosed()){
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ClientManager clientManager = new ClientManager(socket);
            System.out.println("new client connected");
            Thread thread = new Thread(clientManager);
            thread.start();
        }
    }

    private void closeSocket(){
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
