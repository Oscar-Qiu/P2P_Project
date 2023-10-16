import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class peerProcess {
    public int NumberOfPreferredNeighbors;
    public int UnchokingInterval;
    public int OptimisticUnchokingInterval;
    public String FileName;
    public int fileSize;
    public int pieceSize;
    public int currProcessID;

    public peerProcess(){};
    public peerProcess(int id)
    {
        currProcessID = id;
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
                    case "fileSize" -> fileSize = Integer.parseInt(tokens[1]);
                    case "pieceSize" -> pieceSize = Integer.parseInt(tokens[1]);
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
    public static void main(String[] args)
    {
        /*
        System.out.println(args[0]);
        System.out.println("This is my test");
        */
        // To start, read the common.cfg
        peerProcess ps = new peerProcess();
        ps.readCommonCFG();

    }
}
