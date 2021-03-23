package battle;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player extends Thread {


    protected static int port = 5005;
    protected static String address = "127.0.0.1";
    protected static Socket socket;
    protected static boolean running = true;
    private int cellCount = 5;

    public OutputStream output;
    public InputStream input;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private final Scanner scanner = new Scanner(System.in);
    private int[][] field;
    private int[][] fieldEnemy = new int[][]{{0, 0, 0, 0, 0},
                                            {0, 0, 0, 0, 0},
                                            {0, 0, 0, 0, 0},
                                            {0, 0, 0, 0, 0},
                                            {0, 0, 0, 0, 0}};

    int[] coordinatesToShoot = new int[2];

    public static void main(String[] args) {
        new Player();
    }

    public Player() {
        start();
    }

    @Override
    public void run() {
        connect();
        try {
            deployShips();
            showYourBoard();
            boolean goFirst = dataIn.readBoolean();
            if (goFirst) {
                while (running) {
                    ourTurn();
                    if (!running) break;
                    enemyTurn();
                }
            } else {
                while (running) {
                    enemyTurn();
                    if (!running) break;
                    ourTurn();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void ourTurn() throws IOException {
        boolean hit;
        do {
            System.out.println("Our turn!");
            showEnemyBoard();
            makeShot();
            hit = getResult();
            if (dataIn.readBoolean()) {
                System.out.println("We Won!");
                running = false;
                hit = false;
            }
        } while (hit);
    }

    protected void enemyTurn() throws Exception {
        boolean hit;
        do {
            showYourBoard();
            System.out.println("Enemy turn!");
            hit = getHit();
            dataOut.writeBoolean(hit);
            showYourBoard();
            if (cellCount <= 0) {
                System.out.println("We lost!");
                running = false;
                hit = false;
            }
            dataOut.flush();
        } while (hit);
    }

    protected void connect() {
        try {
            socket = new Socket(address, port);
            this.output = socket.getOutputStream();
            this.input = socket.getInputStream();

            this.dataOut = new DataOutputStream(output);
            this.dataIn = new DataInputStream(input);
        } catch (Exception ex) {
            System.out.println("Connection error");
            ex.printStackTrace();
        }
    }

    protected void showYourBoard() {
        System.out.println("\tYour board:");
        for (int i = 0; i < 5; i++) {
            System.out.println();
            for (int j = 0; j < 5; j++) {
                System.out.print(field[i][j] + " ");
            }
        }
        System.out.println();
    }

    protected void showEnemyBoard() {
        System.out.println("\tEnemy board:");
        for (int i = 0; i < 5; i++) {
            System.out.println();
            for (int j = 0; j < 5; j++) {
                System.out.print(fieldEnemy[i][j] + " ");
            }
        }
        System.out.println();
    }

    public void deployShips() throws IOException {
        System.out.println("Deploy your ships!");
        System.out.println("Set up each 5 rows with up to 5 one-cell ships each");
        System.out.println("3 = ship; 0 = empty");
        String row;
        field = new int[5][5];
        int i = 0;
        int j;
        do {
            j = 0;
            do {
                System.out.println("EXAMPLE: 0 0 3 0 0");
                row = scanner.nextLine().trim();
            } while (!row.matches("(\\s*[03]\\s*){5}"));

            for (String s:
                 row.split("")) {
                field[i][j++] = Integer.parseInt(s.trim());
            }
            System.out.println("Got it!\n");
            i++;
        } while (i < 5);

        System.out.println("You are ready for battle!");
        dataOut.writeBoolean(true);
        dataOut.flush();
    }

    public void makeShot() throws IOException {

        System.out.println("Shoot those filthy bastards");
        System.out.print("Coord X:");
        coordinatesToShoot[0] = scanner.nextInt();
        System.out.print("Coord Y:");
        coordinatesToShoot[1] = scanner.nextInt();

        for (int i : coordinatesToShoot) dataOut.writeInt(i);
        dataOut.flush();
    }

    public boolean getHit() throws Exception {

        int[] coordinatesGotHit = new int[2];
        coordinatesGotHit[0] = dataIn.readInt();
        coordinatesGotHit[1] = dataIn.readInt();

        if (field[coordinatesGotHit[0]][coordinatesGotHit[1]] == 3) {
            field[coordinatesGotHit[0]][coordinatesGotHit[1]]--;
            System.out.println("We got hit!");
            cellCount--;
            dataOut.writeBoolean(false); // if same cell
            return true;
        } else if (field[coordinatesGotHit[0]][coordinatesGotHit[1]] == 0) {
            field[coordinatesGotHit[0]][coordinatesGotHit[1]] = 1;
            System.out.println("They've missed!");
            dataOut.writeBoolean(false); // if same cell
            return false;
        } else if (field[coordinatesGotHit[0]][coordinatesGotHit[1]] == 2 ||
                field[coordinatesGotHit[0]][coordinatesGotHit[1]] == 1) {
            System.out.println("Same cell");
            dataOut.writeBoolean(true); // if same cell
            return true;
        }

        throw new Exception("Wrong coordinates: " + coordinatesGotHit[0] + " ; " +
                                                    coordinatesGotHit[1] + " = " +
                                                    field[coordinatesGotHit[0]][coordinatesGotHit[1]]);
    }

    public boolean getResult() throws IOException {

        boolean result = dataIn.readBoolean();

        if (result) {
            fieldEnemy[coordinatesToShoot[0]][coordinatesToShoot[1]] = 2;
        } else {
            fieldEnemy[coordinatesToShoot[0]][coordinatesToShoot[1]] = 1;
        }

        return result;
    }
}