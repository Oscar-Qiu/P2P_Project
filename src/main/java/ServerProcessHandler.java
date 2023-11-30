import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ServerProcessHandler extends Thread {
    public String currID;
    public String peerID;
    Socket connection;
    public Map<String,boolean[]> idToBitField;
    public int pieceSize;
    public String fileName;

    private ServerProcess serverProcess;
    public ObjectOutputStream out;
    public ObjectInputStream in;

    HandShake hs;
    HandleMessage m;

    public ServerProcessHandler() {}

    public ServerProcessHandler(Socket connection, String currID, ServerProcess serverProcess, Map<String,boolean[]> idToBitField, int pieceSize, String fileName) {
        this.connection = connection;
        this.currID = currID;
        this.serverProcess = serverProcess;
        this.idToBitField = idToBitField;
        this.pieceSize = pieceSize;
        this.fileName = fileName;
    }

    // facilitates messaging between client
    public void run() {
        try {
            out = new ObjectOutputStream(this.connection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(this.connection.getInputStream());

            // wait to receive handshake from peer
            hs = new HandShake();
            byte[] receivedMessage = (byte[])in.readObject();
            int receivedPeerID = hs.parseHandShakeMsg(receivedMessage);
            peerID = String.valueOf(receivedPeerID);

            System.out.println("Received peer ID: " + receivedPeerID); // test message

            if(receivedPeerID == ServerProcess.magicKiller){
                this.serverProcess.shouldBreak = true;
            }

            // send this peer's id
            out.writeObject(currID);
            out.flush();

            m = new HandleMessage(currID, peerID, pieceSize, fileName);
            byte msgType;
            boolean interested;
            int pieceIndex;

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
                        interested = m.handleBFMsg(receivedMessage, idToBitField);
                        out.writeObject(m.genBFMsg(idToBitField.get(currID)));

                        break;

                    case 6:
                        System.out.println("Received message (request): ");

                        // send peer the requested piece
                        pieceIndex = m.handleRequest(receivedMessage);
                        System.out.println("Sending piece " + pieceIndex);
                        out.writeObject(m.genPieceMsg(pieceIndex));

                        break;

                    case 7:
                        System.out.println("Received message (piece): ");
                        break;

                    default:
                        System.out.println("Received test message: " + m.getMsgStr(receivedMessage));
                        out.writeObject(m.genStrMsg("Test reply message"));

                        break;
                }


            }


        }
        catch(IOException e){
            System.out.println("Failed to initialize server side stream");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
