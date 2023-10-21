import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
public class PeerManager {
    /*This class is for managing all the peers*/

    /*---The beginning of config info---*/
    public int NumberOfPreferredNeighbors;
    public int UnchokingInterval;
    public int OptimisticUnchokingInterval;
    public String FileName;
    public int FileSize;
    public int PieceSize;
    /*---End of config info---*/

    public String currProcessID;
    // Each peer should keep track of info of other peers
   // List<RemotePeerInfo> peerInfoList = new ArrayList<>();
    // Map the id to each peer
    public Map<String,RemotePeerInfo> peerInfoMap = new HashMap<>();

    public  Map<String,boolean[]> idToBitField = new HashMap<>();
    // Client process manager to send info to other peers
    public ClientProcess c;
    public ServerProcess s;

    public PeerManager(){};
    public PeerManager(String id)
    {
        currProcessID = id;
        System.out.println("The current process id is "+ currProcessID);

    }
    public void init(){

        readCommonCFG();

        readPeerInfo();
        // Set all the bits to 0 if the hasField is 0, otherwise set to true
        initBitField();
        // Start TCP connection to all peers that start before it
        startTCPConnection();


//        System.out.println("nmsl");
    }
    public void readCommonCFG(){
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader("../../../resources/Common.cfg"));

            while((line = in.readLine()) != null) {

                String[] tokens = line.split("\\s+");
                String identifier = tokens[0];
                switch (identifier) {
                    case "NumberOfPreferredNeighbors":
                        NumberOfPreferredNeighbors = Integer.parseInt(tokens[1]);
                        break;
                    case "UnchokingInterval":
                        UnchokingInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "OptimisticUnchokingInterval":
                        OptimisticUnchokingInterval = Integer.parseInt(tokens[1]);
                        break;
                    case "FileName":
                        FileName = tokens[1];
                        break;
                    case "fileSize":
                        FileSize = Integer.parseInt(tokens[1]);
                        break;
                    case "pieceSize":
                        PieceSize = Integer.parseInt(tokens[1]);
                        break;
                    default:
                        break;
                }


            }
           // System.out.println(NumberOfPreferredNeighbors);
            in.close();
            System.out.println("The number of neighbors are "+NumberOfPreferredNeighbors);
        }
        catch(FileNotFoundException e){
            System.out.println("The common.cfg has not been found");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    public void readPeerInfo(){
        String st;
        try {
            BufferedReader in = new BufferedReader(new FileReader("resources/PeerInfo.cfg"));
            while((st = in.readLine()) != null) {

                String[] tokens = st.split("\\s+");
                //System.out.println("tokens begin ----");
                //for (int x=0; x<tokens.length; x++) {
                //    System.out.println(tokens[x]);
                //}
                //System.out.println("tokens end ----");
                peerInfoMap.put(tokens[0],new RemotePeerInfo(tokens[0], tokens[1], tokens[2], tokens[3].equals("1")));

            }

            in.close();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    public int getPieceCount(){
        int res= (FileSize / PieceSize);
        if(FileSize % PieceSize !=0)
        {
            res++;
        }
        return res;
    }
    public void initBitField(){

        for(var peer: peerInfoMap.entrySet())
        {
            boolean[] bitfield = new boolean[getPieceCount()];
            if(peer.getValue().hasFile)
            {
                // Each peer should keep track of its bitfield
                Arrays.fill(bitfield,true);
            } // else set to false
            // Need to store the local variable in the class
            String id = peer.getKey();
            idToBitField.put(id,bitfield);
        }
    }
    public void startTCPConnection(){
//        // Send HandShake message to other peers
        RemotePeerInfo currPeer = peerInfoMap.get(currProcessID);
        String peerAddress = currPeer.peerAddress;
        String port = currPeer.peerPort;
//        // Needs to figure out later on
        c = new ClientProcess(peerAddress,port,currProcessID); // blocking!!!
        s = new ServerProcess(port); // blocking!!!
//

//        // 12:00


    }
    public  static void main(String[] args) throws  Exception{
//        Process serverProcess = Runtime.getRuntime().exec("java ServerProcess.java"); // 2h nonblocking
//        Thread.sleep(2000);
        Process clientProcess = Runtime.getRuntime().exec("java ClientProcess.java"); // 3h  nonblocking
//        serverProcess.getInputStream().transferTo(System.out);
//        serverProcess.getErrorStream().transferTo(System.out);
        clientProcess.getInputStream().transferTo(System.out);
        clientProcess.getErrorStream().transferTo(System.out);
//        serverProcess.waitFor(); // 2:00(right) 5:00(wrong)
        clientProcess.waitFor(); // 3:00(right) 5:00(wrong)
    }
}
