import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class peerProcessManager {
    /*---The beginning of config info---*/
    public int NumberOfPreferredNeighbors;
    public int UnchokingInterval;
    public int OptimisticUnchokingInterval;
    public String FileName;
    public int FileSize;
    public int PieceSize;
    /*---End of config info---*/

    public int currProcessID;
    // Each peer should keep track of info of other peers
   // List<RemotePeerInfo> peerInfoList = new ArrayList<>();
    // Map the id to each peer
    Map<String,RemotePeerInfo> peerInfoMap = new HashMap<>();
    Map<String,boolean[]> idToBitField = new HashMap<>();
    // Client process manager to send info to other peers
    ClientProcess c;


    public peerProcessManager(){};
    public peerProcessManager(int id)
    {
        currProcessID = id;

    }
    public void init(){
        readCommonCFG();
        readPeerInfo();
        // Set all the bits to 0 if the hasField is 0, otherwise set to true
        initBitField();
        // Start TCP connection to all peers that start before it
    }
    public void readCommonCFG(){
        String line;
        try {
            BufferedReader in = new BufferedReader(new FileReader("resources/Common.cfg"));

            while((line = in.readLine()) != null) {

                String[] tokens = line.split("\\s+");
                String identifier = tokens[0];
                switch (identifier)
                {
                    case "NumberOfPreferredNeighbors" -> NumberOfPreferredNeighbors = Integer.parseInt(tokens[1]);
                    case "UnchokingInterval" -> UnchokingInterval = Integer.parseInt(tokens[1]);
                    case "OptimisticUnchokingInterval" -> OptimisticUnchokingInterval = Integer.parseInt(tokens[1]);
                    case "FileName" -> FileName = tokens[1];
                    case "fileSize" -> FileSize = Integer.parseInt(tokens[1]);
                    case "pieceSize" -> PieceSize = Integer.parseInt(tokens[1]);
                }

            }
           // System.out.println(NumberOfPreferredNeighbors);
            in.close();
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
        // Send HandShake message
        // TODO: only establish TCP Connection to peers before it
        c.sendHandShakeMsg();
    }
}
