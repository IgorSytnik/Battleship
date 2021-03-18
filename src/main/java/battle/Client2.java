package battle;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Client2 extends JFrame {

    final int FIELD_SIZE = 10;
//    int[][] playerField = new int[FIELD_SIZE][FIELD_SIZE];
//    int[][] enemyField = new int[FIELD_SIZE][FIELD_SIZE];
    int[][] playerField =  {{0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0}};

    int[][] enemyField =   {{0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0,0,0}};

    static int port = 5005;
    static String adress = "127.0.0.1";
    private ArrayList<Player> players = new ArrayList<Player>();
    private boolean gameOver = false;

    private OutputStream outS;
    private InputStream inputS;
    private ObjectOutputStream out;
    private ObjectInputStream input;

    Canvas rivalPanel;
    Canvas playerPanel;
    JTextArea board;


    public static void main(String[] args) {
        new Client2();
    }

    Client2() {
        try {
            InetAddress ipAdress = InetAddress.getByName(adress);
            Socket socket = new Socket(ipAdress, port);

            inputS = socket.getInputStream();
            outS = socket.getOutputStream();

            input = new ObjectInputStream(inputS);
            out = new ObjectOutputStream(outS);

            setTitle("SeaBattle2");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);

            rivalPanel = new Canvas();
            rivalPanel.setPreferredSize(new Dimension(RIVAL_PANEL_SIZE, RIVAL_PANEL_SIZE));
            rivalPanel.setBackground(Color.black);
            rivalPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {

                    try {
                        if (!gameOver) {
                            if (input.readBoolean()) {  //turn

                                super.mouseReleased(e);
                                int x = e.getY() / RIVAL_CELL_CIZE; // coordinates transformation
                                out.writeInt(x);
                                out.flush();
                                int y = e.getX() / RIVAL_CELL_CIZE;
                                out.writeInt(y);
                                out.flush();
                                int event = e.getButton();
                                out.writeInt(event);
                                out.flush();

                            }
                            gameOver = input.readBoolean();
                            playerField = (int[][]) input.readObject();
                            enemyField = (int[][]) input.readObject();
                            playerPanel.repaint();
                            rivalPanel.repaint();
                        }
                    } catch (Exception ex) {
                        System.out.println("Mouse exception");
                    }

                }
            });

            playerPanel = new Canvas();
            playerPanel.setPreferredSize(new Dimension(PLAYER_PANEL_SIZE, PLAYER_PANEL_SIZE));
            playerPanel.setBackground(Color.black);

            JButton init = new JButton("New game");

            JButton exit = new JButton("Exit");

            board = new JTextArea();
            board.setEditable(false);
            JScrollPane scroll = new JScrollPane(board);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout());
            buttonPanel.add(init);
            buttonPanel.add(exit);

            JPanel container = new JPanel();
            container.setLayout(new BorderLayout());

            container.add(playerPanel, BorderLayout.NORTH);
            container.add(scroll, BorderLayout.CENTER);
            container.add(buttonPanel, BorderLayout.SOUTH);

            setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
            add(rivalPanel);
            add(playerPanel);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);

            playerPanel.repaint();
            rivalPanel.repaint();

        } catch (Exception ex) {
            System.out.println("Connection error");
        }

    }

    void paintShipsAndShots(Graphics g, int cellSize, int[][] field) {
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 9; j++) {

                System.out.print(playerField[i][j] + " ");

                if (field[i][j] == 3) {
                    g.setColor(Color.red);
                    System.out.println("x ="+(i * cellSize + 1));
                    System.out.println("y ="+(j * cellSize + 1));
                    g.fill3DRect(j * cellSize + 1, i * cellSize + 1, cellSize - 2, cellSize - 2, true);
                }
                if (field[i][j] == 2) {
                    g.setColor(Color.blue);
                    g.fill3DRect(j* cellSize + 1, i * cellSize + 1, cellSize - 2, cellSize - 2, true);
                }
                if (field[i][j] == 1) {
                    g.setColor(Color.white);
                    g.fillRect(j * cellSize + cellSize / 2 - 3, i * cellSize + cellSize / 2 - 3, 8, 8);
                }
            }
            System.out.println();
        }
    }

    class Canvas extends JPanel { // for painting

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            int cellSize = (int) getSize().getWidth() / FIELD_SIZE;
            g.setColor(Color.lightGray);
            for (int i = 1; i < FIELD_SIZE; i++) {
                g.drawLine(0, i * cellSize, FIELD_SIZE * cellSize, i * cellSize);
                g.drawLine(i * cellSize, 0, i * cellSize, FIELD_SIZE * cellSize);
            }
            if (cellSize == PLAYER_CELL_CIZE) {
                paintShipsAndShots(g, cellSize, playerField);
                System.out.println("enemy");
            } else {
                paintShipsAndShots(g, cellSize, enemyField);

            }

        }

    }

}