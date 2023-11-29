import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ServerProcessHandler extends Thread {
    public String currID;
    public String peerID;
    Socket connection;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    private ServerProcess serverProcess;
    HandShake hs;
    HandleMessage m;
    public ServerProcessHandler(){};

    public Map<String,boolean[]> idToBitField;

    public ServerProcessHandler(Socket connection, String currID, ServerProcess serverProcess, Map<String,boolean[]> idToBitField) {
        this.connection = connection;
        this.currID = currID;
        this.serverProcess = serverProcess;
        this.idToBitField = idToBitField;
        peerID = "";
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

            peerID = String.valueOf(receivedPeerID);

            if(receivedPeerID == ServerProcess.magicKiller){
                this.serverProcess.shouldBreak = true;
            }
            out.writeObject(currID);
            out.flush();

            m = new HandleMessage();
            byte msgType;
            boolean interested;

            // while loop to receive messages

            while(true) {
                receivedMessage = (byte[])in.readObject();
                msgType = receivedMessage[4];

                switch (msgType) {
                    case 0:
                        System.out.println("Received message (choke): ");
                        break;
                    case 1:
                        System.out.println("Received message (unchoke): ");
                        break;
                    case 2:
                        System.out.println("Received message (interested): ");
                        break;
                    case 3:
                        System.out.println("Received message (not interested): ");
                        break;
                    case 4:
                        System.out.println("Received message (have): ");
                        break;

                    case 5:
                        System.out.println("Received message (bitfield): ");

                        // update bit map of peers & sees if it is interested with it
                        interested = m.handleBFMsg(receivedMessage, idToBitField, currID, peerID);
                        out.writeObject(m.genBFMsg(idToBitField.get(currID)));

                        break;
                    case 6:
                        System.out.println("Received message (request): ");
                        break;
                    case 7:
                        System.out.println("Received message (piece): ");
                        break;
                    default:
                        System.out.println("Received test message: " + m.getMsgStr(receivedMessage));
                        out.writeObject(m.genStrMsg("Test reply message"));
                        break;
                }


               // out.flush();
            }


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
