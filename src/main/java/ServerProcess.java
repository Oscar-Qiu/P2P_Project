import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class ServerProcess extends Thread {
    public ServerSocket listener;
    public int port;
    public String currID;
    public Map<String,boolean[]> idToBitField;
    public int pieceSize;
    public String fileName;

    public volatile boolean shouldBreak = false;
    public static int magicKiller = 999999842;


    public ServerProcess(String port, String currID, Map<String,boolean[]> idToBitField, int pieceSize, String fileName) {
        System.out.println("Trying to bind " + port); // test message
        this.port = Integer.parseInt(port);
        this.currID = currID;
        this.idToBitField = idToBitField;
        this.pieceSize = pieceSize;
        this.fileName = fileName;
        start();
    }

    // Initialize the serverSocket with port number, then wait for peer connections
    public void run() {
        try {
            listener = new ServerSocket(this.port);
            System.out.println("Server created, waiting for peers"); // test message

            // wait for connections
            int numAccept = 0; // used to terminate the number of connections later on
            while(true)
            {
                numAccept += 1;
                new ServerProcessHandler(listener.accept(), currID, this, idToBitField, pieceSize, fileName).start();
                System.out.println("Handling " + numAccept); // test message

//                if(numAccept > 10){
//                    break;
//                }

                if(shouldBreak){
                    System.out.println("Magic number detected Exiting.");
                    System.exit(-1);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("I/O Exception happen when initialize server socket");
            System.out.println(e);
        }
    }

    public static  void main(String[] args) throws  Exception {
        // Send HandShake message to other peers
//        RemotePeerInfo currPeer = peerInfoMap.get(currProcessID);
//        String peerAddress = currPeer.peerAddress;
//        String port = currPeer.peerPort;
        // The server needs to be on before other peers connect to it

//        s = new ServerProcess(port);
//        c = new ClientProcess(peerAddress,port,currProcessID);
        //new ServerProcess("5000");
    }

}
