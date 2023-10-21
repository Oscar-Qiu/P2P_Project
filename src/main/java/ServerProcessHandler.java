import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerProcessHandler extends Thread {
    public int peerID;
    Socket connection;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    HandShake hs;
    public ServerProcessHandler(){};

    public ServerProcessHandler(Socket connection, int peerID) {
        this.connection = connection;
        this.peerID = peerID;
    }

    public void run() {
        try {
            out = new ObjectOutputStream(this.connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(this.connection.getInputStream());
            hs = new HandShake();
            byte[] receivedMessage = (byte[])in.readObject();
            int receivedPeerID = hs.parseHandShakeMsg(receivedMessage);
            System.out.println("Received peer ID: " + receivedPeerID);
            if(this.peerID!=receivedPeerID)
            {
                System.out.println("The peerID is corrupt during transmission");
            }
            out.writeObject("NMSl");
            out.flush();
            // Parse server handshake message from other peers, should uncomment later
//            while(true) {
//                byte[] receivedMsg = (byte[]) in.readObject();
//                hs = new HandShake();
//                hs.parseHandShakeMsg(receivedMsg);
//            }

        }
        catch(IOException e){
            System.out.println("Failed to initialize server side stream");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
