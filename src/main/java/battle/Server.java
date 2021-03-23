package battle;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private int port = 5005;
    private final List<Socket> players = new ArrayList<>();
    private ServerSocket server;
    private List<Thread> gameThreads = new ArrayList<>();

    public static void main(String[] args) {
        Thread server = new Server();
        server.start();
    }

    Server() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server started!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (players) {
                    players.clear();
                    while (players.size() < 2) {
                        players.add(server.accept());
                        System.out.println("Client connected");
                    }
                    gameThreads.add(
                            new Game(players.get(0), players.get(1)));
//                    gameThreads.get(gameThreads.size() - 1).start();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}