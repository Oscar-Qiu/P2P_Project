import java.io.IOException;
import java.net.ServerSocket;

public class ServerProcess extends Thread {
    public ServerSocket listener;
    public int port;
    public String currID;

    public ServerProcess(String port, String currID) {
        this.port = Integer.parseInt(port);
        this.currID = currID;
        start();
    }

    // Initialize the serverSocket with port number and continuously accepts clients
    public void run() {
        try {
            listener = new ServerSocket(this.port);
            System.out.println("Server created, waiting for peers");

            // used to terminate the number of connections later on
            int numAccept = 0;

            while(true) {
                numAccept += 1;
                new ServerProcessHandler(listener.accept(), currID).start();
                System.out.println("Handling " + numAccept);
            }
        }
        catch (IOException e) {
            System.out.println("I/O Exception happen when initialize server socket");
            System.out.println(e);
        }
    }

    public static  void main(String[] args) throws  Exception{
        // Send HandShake message to other peers
//        RemotePeerInfo currPeer = peerInfoMap.get(currProcessID);
//        String peerAddress = currPeer.peerAddress;
//        String port = currPeer.peerPort;
        // The server needs to be on before other peers connect to it

//        s = new ServerProcess(port);
//        c = new ClientProcess(peerAddress,port,currProcessID);
        new ServerProcess("5000", "1000");
    }

}
