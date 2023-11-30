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

public class ClientProcess extends Thread {
    public Socket requestSocket;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    HandShake hs;
    HandleMessage m;
    public String currID;
    public int peerID;
    public int port;
    String peerAddress;
    PeerManager p;

    public Map<String,boolean[]> idToBitField;
    public boolean[] bitField;

    public ClientProcess() {
    }

    public ClientProcess(String peerAddress, String port, String currID, String peerID, Map<String,boolean[]> idToBitField) {
        this.currID = currID;
        this.peerID = Integer.parseInt(peerID);
        this.port = Integer.parseInt(port);
        this.peerAddress = peerAddress;

        this.idToBitField = idToBitField;

        start();
    }


    // Attempt to connect to peer with given address and port
    public void run() {
        try {
            peerAddress = "localhost"; // added to test locally, change to target address
            System.out.println("Attempting to handshake peer " + peerID);

            requestSocket = new Socket(peerAddress, port);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            // Send Handshake message only to the connected peer
            boolean hsMessage = sendHandShakeMsg();
            if (!hsMessage) {
                System.out.println("Connected to wrong peer");

                // exit and close
            }

            m = new HandleMessage();
            byte[] receivedMessage;

            // Test message
            System.out.println("Sending a test message");
            out.writeObject(m.genStrMsg("Client Test Message"));
            // out.flush();

            receivedMessage = (byte[]) in.readObject();
            // System.out.println("Peer's reply: " + m.getMsgStr(receivedMessage));


            // sending initial bitField message
            System.out.println("Sending a bitField message");
            out.writeObject(m.genBFMsg(idToBitField.get(currID)));
            // out.flush();


            receivedMessage = (byte[]) in.readObject();
            // System.out.println("Peer's reply: " + m.getMsgBF(receivedMessage));
            
            Arrays.fill(idToBitField.get(currID), true); // testing







        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {

            //Close connections
            try {
                System.out.println("Closing client");

                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
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
            System.out.print("Handshake response message: ");
            String s = (String) in.readObject();
            System.out.println(s);

            if (peerID != Integer.parseInt(s)) {
                return false;
            }
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        return true;
    }

    public static void main(String[] args) throws Exception {
        ClientProcess c = new ClientProcess("localhost", "5000", "1000", "1001", new HashMap<>());
    }
}