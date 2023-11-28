import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Killer {

    public static void main(String[] args) throws  Exception{
        StartRemotePeers startRemotePeers = new StartRemotePeers();
        startRemotePeers.getConfiguration();
//        for(RemotePeerInfo peerManager: startRemotePeers.peerInfoVector){
        for(int i = startRemotePeers.peerInfoVector.size()- 1; i >=0; i--){
            RemotePeerInfo peerManager = startRemotePeers.peerInfoVector.get(i);
            System.out.println("Killing "+ peerManager.peerAddress);
            try{
                String peerAddress = peerManager.peerAddress;
                String port = peerManager.peerPort;
                int id = ServerProcess.magicKiller;
                ClientProcess clientProcess = new ClientProcess(peerAddress, port, String.valueOf(id),peerManager.peerId);
                clientProcess.sendHandShakeMsg();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
