package battle;

import java.io.IOException;

public class Game implements Runnable {

    private Player player1;
    private Player player2;

    private int cellCountP1 = 10;
    private int cellCountP2 = 10;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    public void run() {
        try {
            player1.deployShips();
            player2.deployShips();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean hit;

        try {
            while (cellCountP1 > 0 && cellCountP2 > 0) {
                hit = false;

                // player1's turn
                do {
                    /*
                    * out.write(success ? 1 : 0);
                    * boolean success = in.read() != 0;
                    * */
                    player2.output.write(player1.input.read());
                    hit = player2.input.read() != 0;
                    if (hit) {
                        cellCountP2--;
                        continue;
                    }
                    hit = false;
                } while (cellCountP1 > 0 && cellCountP2 > 0 && hit);

                // player2's turn
                do {
                    /*
                     * out.write(success ? 1 : 0);
                     * boolean success = in.read() != 0;
                     * */
                    player1.output.write(player2.input.read());
                    hit = player1.input.read() != 0;
                    if (hit) {
                        cellCountP1--;
                        continue;
                    }
                    hit = false;
                } while (cellCountP1 > 0 && cellCountP2 > 0 && hit);

            }

            if (cellCountP1 <= 0 && cellCountP2 <= 0) { // If somehow both players will lose
                throw new Exception("Both players lost");
            }
            // Summary
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
