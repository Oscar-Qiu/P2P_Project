import java.io.IOException;
import java.net.ServerSocket;

public class ServerProcess {
    public ServerSocket listener;
    public int peerId;
    public int port;
    public ServerProcess(int peerId,int port)
    {
        this.peerId = peerId;
        this.port = port;
        // Initialize the serverSocket with port number
        try {
            listener = new ServerSocket(port);
            while(true)
            {
                new ServerProcessHandler(listener.accept(),peerId).start();
            }
        }
        catch (IOException e)
        {
            System.out.println("I/O Exception happen when initialize server socket");
        }
    }
}
