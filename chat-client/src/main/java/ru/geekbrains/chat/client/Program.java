package ru.geekbrains.chat.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("inter your name: ");

            String name = scanner.nextLine();
            Socket socket = new Socket("192.168.1.13", 1400);
            Client client = new Client(socket, name);
            InetAddress inetAddress = socket.getInetAddress();
            System.out.println("inetAddress: " + inetAddress);
            String remoteIp = inetAddress.getHostAddress();
            System.out.println("remote ip: " + remoteIp);
            System.out.println("localPort: " + socket.getLocalPort());

            client.listenForMessage();
            client.sendMessage();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
