import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientProcess extends Thread {
    public Socket requestSocket;
    public ObjectOutputStream out;
    public ObjectInputStream in;

    HandShake hs;
    HandleMessage m;

    public String currID;
    public String peerID;
    public int port;
    String peerAddress;
    public Map<String,boolean[]> idToBitField;
    public int pieceSize;
    public String fileName;

    public ClientProcess() {}

    public ClientProcess(String peerAddress, String port, String currID, String peerID, Map<String,boolean[]> idToBitField, int pieceSize, String fileName) {
        this.currID = currID;
        this.peerID = peerID;
        this.port = Integer.parseInt(port);
        this.peerAddress = peerAddress;
        this.idToBitField = idToBitField;
        this.pieceSize = pieceSize;
        this.fileName = fileName;
        start();
    }

    // Attempt to connect to peer with given address and port
    public void run() {
        try {
            peerAddress = "localhost"; // added to test locally, change to target address
            System.out.println("Attempting to handshake peer " + peerID); // test message

            requestSocket = new Socket(peerAddress, port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            // Send Handshake message to the connected peer
            boolean hsMessage = sendHandShakeMsg();
            if (!hsMessage) {
                System.out.println("Connected to wrong peer");

                // should exit and close if wrong peer
            }

            m = new HandleMessage(currID, peerID, pieceSize, fileName);
            byte[] receivedMessage;
            boolean interested;

            // Test message
            System.out.println("Sending a test message");
            out.writeObject(m.genStrMsg("Client Test Message"));

            receivedMessage = (byte[]) in.readObject();
            System.out.println("Peer's reply: " + m.getMsgStr(receivedMessage));

            // sending initial bitField message
            System.out.println("Sending a bitField message");
            out.writeObject(m.genBFMsg(idToBitField.get(currID)));

            // receiving peer's bitField
            receivedMessage = (byte[]) in.readObject();
            System.out.println("Peer's reply: ");
            interested = m.handleBFMsg(receivedMessage, idToBitField);

            if(interested) {
                out.writeObject(m.genIntMsg());
            }
            else {
                out.writeObject(m.genNotIntMsg());
            }

            // find next piece to request and continuously request until there are none left
            int pieceIndex = findPieceToRequest();

            while(pieceIndex != -1) {
                out.writeObject(m.genReqMsg(pieceIndex));
                receivedMessage = (byte[]) in.readObject();
                m.handlePiece(receivedMessage, idToBitField);

                pieceIndex = findPieceToRequest(); // find new piece
            }

            System.out.println("File download done");


            System.out.println("");

            System.out.println("this peer's bitField");

            for(int i = 0; i < idToBitField.get(currID).length; i++){
                if(idToBitField.get(peerID)[i]) { System.out.print("1"); }
                else { System.out.print("0"); }
            }

            System.out.println("");



        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
        catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            //Close connections
            try {
                System.out.println("Closing client");

                in.close();
                out.close();
                requestSocket.close();
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    // Send handshake message and return false if server rejects or connected to wrong peer
    public boolean sendHandShakeMsg() throws IOException {
        hs = new HandShake();
        byte[] messageToSend = hs.generateHandShakeMsg(Integer.parseInt(currID));

        try {
            out.writeObject(messageToSend);
            out.flush();

            // incoming message from server
            String s = (String) in.readObject();
            System.out.println("Handshake response message: " + s); // test message

            // if connected to wrong peer, return false
            if (!s.equals(peerID)) {
                return false;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }

        return true;
    }

    // finds a needed piece the peer has, if there is not one then return -1
    public int findPieceToRequest() {
        int pieceIndex = -1;

        // find the piece indexes of all pieces the peer has that this peer does not have
        ArrayList<Integer> neededPieces = new ArrayList<>();

        for(int i = 0; i < idToBitField.get(currID).length; i++) {
            if((!idToBitField.get(currID)[i]) && idToBitField.get(peerID)[i]) {
                neededPieces.add(i);
            }
        }

        // select a random piece
        Random rand = new Random();

        if(neededPieces.size() != 0) {
            pieceIndex = neededPieces.get(rand.nextInt(neededPieces.size()));
        }

        return pieceIndex;
    }

    public static void main(String[] args) throws Exception {
        ClientProcess c = new ClientProcess("localhost", "5000", "1000", "1001", new HashMap<>(), 0, "");
    }
}