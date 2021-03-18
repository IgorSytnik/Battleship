package battle;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    static int port = 5005;
    private final ArrayList<Player> players = new ArrayList<>();
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private List<Thread> gameThreads;

    public static void main(String[] args) {
        Thread server = new Thread(new Server()) ;
        server.start();
    }

    Server() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
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
                        players.add(new Player(serverSocket.accept()));
                        System.out.println("Client connected");
                    }
                    gameThreads.add(new Thread(
                            new Game(players.get(0), players.get(1))));
                    gameThreads.get(gameThreads.size() - 1).start();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}