import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerProcessHandler extends Thread {
    public String currID;
    Socket connection;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    private ServerProcess serverProcess;
    HandShake hs;
    HandleMessage m;
    public ServerProcessHandler(){};

    public ServerProcessHandler(Socket connection, String currID, ServerProcess serverProcess) {
        this.connection = connection;
        this.currID = currID;
        this.serverProcess = serverProcess;
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

            if(receivedPeerID == ServerProcess.magicKiller){
                this.serverProcess.shouldBreak = true;
            }
            out.writeObject(currID);
            out.flush();

            m = new HandleMessage();
            byte msgType;
            byte[] msgBytes;
            String msg;

            // while loop to receive messages
            /*
            while(true) {
                receivedMessage = (byte[])in.readObject();
                msgType = receivedMessage[4];                   // can place this elsewhere or not idk
                msg = new String(m.getMsg(receivedMessage));   // not sure if getMsg should return string or not

                // Switch statement should go in another class (HandleMessage) maybe
                switch (msgType) {
                    case 0:
                        System.out.println("Received message (choke): " + msg);
                        break;
                    case 1:
                        System.out.println("Received message (unchoke): " + msg);
                        break;
                    case 2:
                        System.out.println("Received message (interested): " + msg);
                        break;
                    case 3:
                        System.out.println("Received message (not interested): " + msg);
                        break;
                    case 4:
                        System.out.println("Received message (have): " + msg);
                        break;
                    case 5:
                        System.out.println("Received message (bitfield): " + msg);
                        break;
                    case 6:
                        System.out.println("Received message (request): " + msg);
                        break;
                    case 7:
                        System.out.println("Received message (piece): " + msg);
                        break;
                    default:
                        break;
                }

                out.writeObject(m.generateMsg(4, "Test reply message"));    // testing
            }

             */

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
