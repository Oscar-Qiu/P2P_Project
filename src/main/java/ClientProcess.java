import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientProcess {

    public Socket requestSocket;
    public ObjectOutputStream out;
    public ObjectInputStream in;
    HandShake hs;
    public int currID;

    public ClientProcess(String peerAddress, String port,String currID)
    {
        this.currID = Integer.parseInt(currID);
        try {
            requestSocket = new Socket(peerAddress, Integer.parseInt(port));
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());
            // send header message
            sendHandShakeMsg();
        }
        catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        }
        catch(UnknownHostException unknownHost){
            System.err.println("You are trying to connect to an unknown host!");
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
        finally{
            //Close connections
            try{
                in.close();
                out.close();
                requestSocket.close();
            }
            catch(IOException ioException){
                ioException.printStackTrace();
            }
        }

    }
    public void sendHandShakeMsg(){
        hs = new HandShake(currID);
        try {
            byte[] msgToSend = hs.generateHandShakeMsg();
            out.writeObject(msgToSend);
        }
        catch(IOException e)
        {
            System.out.println("I/O Exception happen");
        }
    }

}
