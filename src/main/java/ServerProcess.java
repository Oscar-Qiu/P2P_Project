import java.io.IOException;
import java.net.ServerSocket;

public class ServerProcess extends Thread {
    public ServerSocket listener;
    public int port;
    public int peerID;
    public volatile boolean shouldBreak = false;
    public static int magicKiller = 999999842;

    public ServerProcess(String port)
    {
        System.out.println("Trying to bind " + port);
        this.port = Integer.parseInt(port);
        start();
    }

    public void run() {
        // Initialize the serverSocket with port number
        try {
            listener = new ServerSocket(this.port);
            System.out.println("Creating server");
            // used to terminate the number of connections later on
            int numAccept = 0;
            while(true)
            {
                numAccept += 1;
                new ServerProcessHandler(listener.accept(), 1000, this).start();
                System.out.println("Handling " + numAccept);
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

    public static  void main(String[] args) throws  Exception{
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
