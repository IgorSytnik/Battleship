package battle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Player {

    private Socket clientSocket;
    public OutputStream output;
    public InputStream input;
    private int[][] field =    {{0,0,0,0,0},
                                {0,0,3,0,0},
                                {0,0,3,0,0},
                                {0,0,0,0,3},
                                {0,3,0,0,3}};

    public Player(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.output = clientSocket.getOutputStream();
        this.input = clientSocket.getInputStream();
    }

    public int[] deployShips() throws Exception {
        throw new Exception("Not implemented yet");
    }

    public int[] makeShot() throws Exception {
        throw new Exception("Not implemented yet");
    }

    public boolean getHit() throws Exception {
        throw new Exception("Not implemented yet");
    }

    // And maybe add more
}