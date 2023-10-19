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

    public ServerProcessHandler(Socket connection,int peerID){
        this.connection = connection;
        this.peerID = peerID;
    }
    public void run(){
        try {
            out = new ObjectOutputStream(this.connection.getOutputStream());
            in = new ObjectInputStream(this.connection.getInputStream());
            // Parse server handshake message
            byte[] receivedMsg = (byte[]) in.readObject();
            hs = new HandShake(peerID);
            hs.parseHandShakeMsg(receivedMsg);

        }
        catch(IOException e){
            System.out.println("Failed to initialize server side stream");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
