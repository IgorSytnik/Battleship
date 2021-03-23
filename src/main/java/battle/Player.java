package battle;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Player extends Thread {


    protected static int port = 5005;
    protected static String address = "127.0.0.1";
    protected static Socket socket;
    protected static boolean running = true;

    public OutputStream output;
    public InputStream input;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private final Scanner scanner = new Scanner(System.in);
    private int[][] field;

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
        deployShips();
        try {
            boolean goFirst = dataIn.readBoolean();
            if (goFirst) {
                while (running) {
                    boolean hit;
                    do {
                        System.out.println("Our turn!");
                        makeShot();
                        hit = getResult();
                    } while (hit);

                    do {
                        System.out.println("Enemy turn!");
                        hit = getHit();
                        dataOut.writeBoolean(hit);
                        dataOut.flush();
                    } while (hit);
                }
            } else {
                while (running) {
                    boolean hit;
                    do {
                        hit = getHit();
                        dataOut.writeBoolean(hit);
                        dataOut.flush();
                    } while (hit);

                    do {
                        makeShot();
                        hit = getResult();
                    } while (hit);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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

    public void deployShips() {
        field = new int[][]{{0, 0, 0, 0, 0},
                            {0, 0, 3, 0, 0},
                            {0, 0, 3, 0, 0},
                            {0, 0, 0, 0, 3},
                            {0, 3, 0, 0, 3}};
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
            return true;
        } else if (field[coordinatesGotHit[0]][coordinatesGotHit[1]] == 0) {
            field[coordinatesGotHit[0]][coordinatesGotHit[1]] = 1;
            System.out.println("They've missed!");
            return false;
        }

        throw new Exception("Wrong coordinates: " + coordinatesGotHit[0] + " ; " +
                                                    coordinatesGotHit[1] + " = " +
                                                    field[coordinatesGotHit[0]][coordinatesGotHit[1]]);
    }

    public boolean getResult() throws IOException {

        boolean result = dataIn.readBoolean();

        if (result) {
            field[coordinatesToShoot[0]][coordinatesToShoot[1]] = 2;
        } else {
            field[coordinatesToShoot[0]][coordinatesToShoot[1]] = 1;
        }

        return result;
    }

    // And maybe add more
}