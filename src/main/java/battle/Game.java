package battle;

import java.io.*;
import java.net.Socket;

public class Game extends Thread {

    private Socket player1;
    private Socket player2;
    private final DataOutputStream player1Out;
    private final DataInputStream player1In;
    private final DataOutputStream player2Out;
    private final DataInputStream player2In;

    private int cellCountP1 = 5;
    private int cellCountP2 = 5;

    public Game(Socket player1, Socket player2) throws IOException {
        this.player1 = player1;
        this.player2 = player2;
        this.player1In = new DataInputStream(player1.getInputStream());
        this.player2In = new DataInputStream(player2.getInputStream());
        this.player1Out = new DataOutputStream(player1.getOutputStream());
        this.player2Out = new DataOutputStream(player2.getOutputStream());
        start();
    }

    @Override
    public void run() {
//        try {
//            player1.deployShips();
//            player2.deployShips();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        boolean hit;

        try {
            player1Out.writeBoolean(true);
            player2Out.writeBoolean(false);
            while (cellCountP1 > 0 && cellCountP2 > 0) {

                // Player1 is going first
                // player1's turn
                do {
                    /*
                    * out.write(success ? 1 : 0);
                    * boolean success = in.read() != 0;
                    * */
//                    player1.makeShot();
                    System.out.println("Player1's turn");
                    // X
                    player2Out.writeInt(player1In.readInt());
                    // Y
                    player2Out.writeInt(player1In.readInt());
                    hit = player2In.readBoolean();
                    player1Out.writeBoolean(hit);
                    if (hit) {
                        cellCountP2--;
                    }
                } while (cellCountP1 > 0 && cellCountP2 > 0 && hit);

                if (cellCountP1 <= 0 || cellCountP2 <= 0) break;

                // player2's turn
                do {
                    System.out.println("Player2's turn");
                    // X
                    player1Out.writeInt(player2In.readInt());
                    // Y
                    player1Out.writeInt(player2In.readInt());
                    hit = player1In.readBoolean();
                    player2Out.writeBoolean(hit);
                    if (hit) {
                        cellCountP1--;
                    }
                } while (cellCountP1 > 0 && cellCountP2 > 0 && hit);

            }

            if (cellCountP1 <= 0 && cellCountP2 <= 0) { // If somehow both players will lose
                throw new Exception("Both players lost");
            }
            System.out.println("Player1's ships: " + cellCountP1 + ", Player2's ships: " + cellCountP2);
            // Summary
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
