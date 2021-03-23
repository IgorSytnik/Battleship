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

        boolean hit, sameCell;

        try {
            if (player1In.readBoolean()) {              // deploy ships
                if (player2In.readBoolean()) {
                    System.out.println("Game starts");
                }
            }
            player1Out.writeBoolean(true);  // first to go
            player2Out.writeBoolean(false); // second to go
            while (cellCountP1 > 0 && cellCountP2 > 0) {

                // Player1 is going first
                // player1's turn
                do {
                    System.out.println("Player1's turn");
                    // X
                    player2Out.writeInt(player1In.readInt());
                    // Y
                    player2Out.writeInt(player1In.readInt());
                    player2Out.flush();
                    sameCell = player2In.readBoolean(); // P1 shot in the already shot cell
                    hit = player2In.readBoolean();  // P2 got hit?
                    player1Out.writeBoolean(hit);
                    if (hit && !sameCell) {
                        cellCountP2--;
                    }
                    player1Out.writeBoolean(cellCountP2 <= 0); // P2 lost?
                    player1Out.flush();
                } while (cellCountP1 > 0 && cellCountP2 > 0 && hit);

                if (cellCountP1 <= 0 || cellCountP2 <= 0) break;

                // player2's turn
                do {
                    System.out.println("Player2's turn");
                    // X
                    player1Out.writeInt(player2In.readInt());
                    // Y
                    player1Out.writeInt(player2In.readInt());
                    player1Out.flush();
                    sameCell = player1In.readBoolean();
                    hit = player1In.readBoolean();
                    player2Out.writeBoolean(hit);
                    if (hit && !sameCell) {
                        cellCountP1--;
                    }
                    player2Out.writeBoolean(cellCountP1 <= 0); // lost?
                    player2Out.flush();
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
