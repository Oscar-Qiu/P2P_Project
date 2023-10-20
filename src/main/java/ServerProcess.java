import java.io.IOException;
import java.net.ServerSocket;

public class ServerProcess {
    public ServerSocket listener;
    public int port;
    public ServerProcess(String port)
    {

        this.port = Integer.parseInt(port);
        // Initialize the serverSocket with port number
        try {
            listener = new ServerSocket(this.port);
            // used to terminate the number of connections later on
            int numAccept = 0;
            while(true)
            {
                numAccept += 1;
                new ServerProcessHandler(listener.accept(),1000).start();
                System.out.println("Handling " + numAccept);
//                if(numAccept > 10){
//                    break;
//                }
            }
        }
        catch (IOException e)
        {
            System.out.println("I/O Exception happen when initialize server socket");
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
        new ServerProcess("5000");
    }

}
