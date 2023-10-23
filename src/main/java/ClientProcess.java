import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientProcess extends Thread {
    public Socket requestSocket;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    HandShake hs;
    HandleMessage m;
    public int currID;
    public int peerID;
    public int port;
    String peerAddress;
    PeerManager p;

    public ClientProcess() {}
    public ClientProcess(String peerAddress, String port, String currID, String peerID) {
        this.currID = Integer.parseInt(currID);
        this.peerID = Integer.parseInt(peerID);
        this.port = Integer.parseInt(port);
        this.peerAddress = peerAddress;
        start();
    }


    // Attempt to connect to peer with given address and port
    public void run() {
        try {
            // peerAddress = "localhost"; // added to test locally, change to target address
            System.out.println("Attempting to handshake peer " + peerID);

            requestSocket = new Socket(peerAddress, port);

            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            // Send Handshake message only to the connected peer
            boolean hsMessage = sendHandShakeMsg();
            if(!hsMessage) {
                System.out.println("Connected to wrong peer");

                // exit and close

            }

            // Test message
            /*
            m = new HandleMessage();
            System.out.println("Sending a test message");
            out.writeObject(m.generateMsg(3, "Test send message"));

            byte[] receivedMessage;
            String msg;

            // while loop to send messages, ignore for now
                receivedMessage = (byte[])in.readObject();
                msg = new String(m.getMsg(receivedMessage)); // not sure if  getMsg should return string or not
                System.out.println("Peer's reply: " + msg);

             */

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
        /*
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
         */
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
    //}

    // Send handshake message and return false if server rejects or connected to wrong peer
    public boolean sendHandShakeMsg() throws IOException {
        hs = new HandShake();
        byte[] messageToSend = hs.generateHandShakeMsg(currID);

        try {
            out.writeObject(messageToSend);
            out.flush();

            // incoming message from server
            System.out.print("Handshake response message: ");
            String s = (String)in.readObject();
            System.out.println(s);

            if(peerID != Integer.parseInt(s)) {
                return false;
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        return true;
    }

    public static void main(String[] args) throws Exception {
        ClientProcess c = new ClientProcess("localhost", "5000", "1000", "1001");
    }
}