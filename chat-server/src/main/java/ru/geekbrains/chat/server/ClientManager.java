package ru.geekbrains.chat.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager implements Runnable{

    // region Fields
    private final Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String name;
    public final static ArrayList<ClientManager> clients = new ArrayList<>();
    // endregion

    // region Constructors
    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            name = bufferedReader.readLine();
            clients.add(this);
            System.out.println(name + " connected to chat.");
            broadcastMessage("Server: " + name + " connected to chat.");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    // endregion

    // region Methods
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    private void broadcastMessage(String message){
        String direct = isMessageDirect(message);
        if (!direct.equals(name)){
           sendDirectMessage(message, direct);
        } else {
            sendPublicMessage(message);
        }
    }

    private void sendDirectMessage(String message, String direct){
        ClientManager client = clients.stream().filter(c -> c.name.equals(direct)).findFirst().orElse(null);
        try {
            if (client != null){
                client.bufferedWriter.write(message);
                client.bufferedWriter.newLine();
                client.bufferedWriter.flush();
            } else {
                bufferedWriter.write("client is not found");
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void sendPublicMessage(String message){
        clients.stream().filter(client -> !(client.name.equals(name))).forEach(client -> {
            try {
                client.bufferedWriter.write(message);
                client.bufferedWriter.newLine();
                client.bufferedWriter.flush();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        });
    }

    /**
     * Метод проверки сообщения на лс.
     * @param message - сообщение
     * @return Возращает имя получателя, если сообщение личное, иначе возвращает имя отправителя.
     */
    private String isMessageDirect(String message){
        String[] str = message.split(" ");
        for (int i = 0; i < str.length; i++) {
            if (str[i].startsWith("@")){
                return str[i].substring(1, str[i].length());
            }
        }
        return name;
    }

    private void removeClient(){
        clients.remove(this);
        System.out.println(name + " disconnected.");
        broadcastMessage("Server: " + name + " disconnected.");
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClient();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // endregion
}
