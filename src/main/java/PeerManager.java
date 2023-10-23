import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    public ArrayList<String> peerIDs = new ArrayList<>();
    public ArrayList<String> peerAddress = new ArrayList<>();

    public ClientProcess c;
    public ServerProcess s;

    public PeerManager() {}
    public PeerManager(String id) {
        currProcessID = id;
        System.out.println("The current process id is "+ currProcessID);
    }

    // Read config files, set bit field, and start TCP connections
    public void init() {
        readCommonCFG();
        readPeerInfo();

        // Set all the bits to 0 if the hasField is 0, otherwise set to true
        initBitField();

        // Start TCP connection to all peers that start before it
        try {
            startTCPConnection();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Read Common.cfg
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
                    case "FileSize":
                        FileSize = Integer.parseInt(tokens[1]);
                        break;
                    case "PieceSize":
                        PieceSize = Integer.parseInt(tokens[1]);
                        break;
                    default:
                        break;
                }
            }
            in.close();
        }
        catch(FileNotFoundException e) {
            System.out.println("The common.cfg has not been found");
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // Read PeerInfo.cfg
    public void readPeerInfo() {
        String st;

        try {
            BufferedReader in = new BufferedReader(new FileReader("../../../resources/PeerInfo.cfg"));
            while((st = in.readLine()) != null) {
                String[] tokens = st.split("\\s+");

                //System.out.println("tokens begin ----");
                //for (int x=0; x<tokens.length; x++) {
                //    System.out.println(tokens[x]);
                //}
                //System.out.println("tokens end ----");

                peerInfoMap.put(tokens[0], new RemotePeerInfo(tokens[0], tokens[1], tokens[2], tokens[3].equals("1")));

                peerIDs.add(tokens[0]);
                peerAddress.add(tokens[1]);
            }
            in.close();
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    // Calculate piece count
    public int getPieceCount() {
        int res = (FileSize / PieceSize);

        if(FileSize % PieceSize != 0) {
            res++;
        }

        return res;
    }

    // Initialize bit field, if the peer has the file, the field will all be true, else false
    public void initBitField() {
        for(Map.Entry<String, RemotePeerInfo> peer: peerInfoMap.entrySet())
        {
            boolean[] bitfield = new boolean[getPieceCount()];

            if(peer.getValue().hasFile)
            {
                Arrays.fill(bitfield,true); // Each peer should keep track of its bitfield
            }
            // else set to false

            // Need to store the local variable in the class
            String id = peer.getKey();
            idToBitField.put(id,bitfield);
        }
    }

    // Initiate server and TCP connections to all peers before it
    // Throws Interrupted Exception is for sleep call
    public void startTCPConnection() throws InterruptedException {
        RemotePeerInfo currPeer = peerInfoMap.get(currProcessID);
        String peerAddress = currPeer.peerAddress;
        String port = currPeer.peerPort;

        s = new ServerProcess(port, currProcessID);
        TimeUnit.SECONDS.sleep(1); // Used to delay so test messages do not overlap

        // Find which peers to send a connection to (only connect to prev peers in PeerInfo.cfg)
        String p;
        for(int i = 0; i < peerIDs.size(); i++)
        {
            p = peerIDs.get(i);

            if(p.equals(currProcessID)) {
                i = peerIDs.size();
            }
            else {
                c = new ClientProcess(peerInfoMap.get(p).peerAddress, peerInfoMap.get(p).peerPort, currProcessID, p);
            }

            TimeUnit.SECONDS.sleep(1); // Used to delay so test messages do not overlap
        }

    }

    public  static void main(String[] args) throws  Exception{
//        Process serverProcess = Runtime.getRuntime().exec("java ServerProcess.java"); // 2h nonblocking
//        Thread.sleep(2000);
        Process clientProcess = Runtime.getRuntime().exec("java ClientProcess.java"); // 3h  nonblocking
//        serverProcess.getInputStream().transferTo(System.out);
//        serverProcess.getErrorStream().transferTo(System.out);
        //clientProcess.getInputStream().transferTo(System.out);
        //clientProcess.getErrorStream().transferTo(System.out);
//        serverProcess.waitFor(); // 2:00(right) 5:00(wrong)
        clientProcess.waitFor(); // 3:00(right) 5:00(wrong)
    }
}
