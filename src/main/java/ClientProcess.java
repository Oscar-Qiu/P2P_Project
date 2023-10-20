import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientProcess {

    public Socket requestSocket;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    HandShake hs;
    public int currID;
    PeerManager p;

    public ClientProcess() {
    }

    ;

    public ClientProcess(String peerAddress, String port, String currID) {
        this.currID = Integer.parseInt(currID);
        try {
            requestSocket = new Socket(peerAddress, Integer.parseInt(port));
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            // Send Handshake message only to the peerID before it
            p = new PeerManager();
            sendHandShakeMsg();
//            for(var peer: p.peerInfoMap.entrySet())
//            {
//                int peerID = Integer.parseInt(peer.getKey());
//                if(peerID < this.currID)
//                {
//                    sendHandShakeMsg();
//                }
//                break;
//            }

        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //Close connections
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    public void sendHandShakeMsg() throws IOException {
        hs = new HandShake();
        byte[] messageToSend = hs.generateHandShakeMsg(currID);

        try {
//            byte[] msgToSend = hs.generateHandShakeMsg();
//            out.writeObject(msgToSend);
            // Send nmsl to server and wait for NMSL
            // out.writeObject("nmsl");
            out.writeObject(messageToSend);
            out.flush();
            // incoming message from server
            String s = (String) in.readObject();
            System.out.println(s);
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {

        }


    }

    public static void main(String[] args) throws Exception {
        ClientProcess c = new ClientProcess("localhost", "5000", "1000");
    }
}
