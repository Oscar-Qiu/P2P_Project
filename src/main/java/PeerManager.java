import java.io.*;
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
    // Map the id to each peer
    public Map<String, RemotePeerInfo> peerInfoMap = new HashMap<>(); // <peerID, peerInfo (class)>
    public  Map<String, boolean[]> idToBitField = new HashMap<>();    // <peerID, bitField (bool[])>
    public ArrayList<String> peerIDs = new ArrayList<>();             // list of peer id's in the order in peerInfo.cfg

    public ClientProcess c;
    public ServerProcess s;

    public PeerManager() {}
    public PeerManager(String id) {
        currProcessID = id;
        System.out.println("The current process id is "+ currProcessID); // test message
    }

    // Read config files, set bit field, generate a junk file if the peer does not have a file, and start TCP connections
    public void init() {
        readCommonCFG();
        readPeerInfo();

        // Set all the bits to 0 if the hasField is 0, otherwise set to true
        initBitField();

        // create a dummy file if the peer does not have the file
        try {
            createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start TCP connection to all peers that start before it
        try {
            startTCPConnection();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Read Common.cfg
    public void readCommonCFG() {
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

                peerInfoMap.put(tokens[0], new RemotePeerInfo(tokens[0], tokens[1], tokens[2], tokens[3].equals("1")));
                peerIDs.add(tokens[0]);
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
            int size = getPieceCount();
            boolean[] bitfield = new boolean[size];

            // setting bit field
            if(peer.getValue().hasFile)
            {
                Arrays.fill(bitfield,true);
            }

            // Need to store the local variable in the class
            String id = peer.getKey();
            idToBitField.put(id,bitfield);
        }
    }

    // creates a junk file of the appropriate size if the peer does not have the file
    public void createFile() throws IOException, IOException {
        if(!peerInfoMap.get(currProcessID).hasFile) {
            File file = new File("../../../peer_" + currProcessID + "/" + FileName);
            file.createNewFile();

            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.setLength(FileSize);
            raf.close();
        }
    }

    // Initiate server and TCP connections to all peers before it
    // Throws Interrupted Exception is for sleep call
    public void startTCPConnection() throws InterruptedException {
        RemotePeerInfo currPeer = peerInfoMap.get(currProcessID);
        String port = currPeer.peerPort;

        // create server for peer
        s = new ServerProcess(port, currProcessID, idToBitField, PieceSize, FileName);

        TimeUnit.SECONDS.sleep(1); // Used to delay so test messages do not overlap

        // Find which peers to send a connection to (only connect to prev peers in PeerInfo.cfg)
        String p;
        for(int i = 0; i < peerIDs.size(); i++)
        {
            p = peerIDs.get(i);

            // create client for peer (if applicable)
            if(p.equals(currProcessID)) {
                i = peerIDs.size();
            }
            else {
                c = new ClientProcess(peerInfoMap.get(p).peerAddress, peerInfoMap.get(p).peerPort, currProcessID, p, idToBitField, PieceSize, FileName);
            }

            TimeUnit.SECONDS.sleep(1); // Used to delay so test messages do not overlap
        }
    }

    public  static void main(String[] args) throws  Exception {
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