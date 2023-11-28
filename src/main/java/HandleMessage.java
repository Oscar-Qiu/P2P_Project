import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.BitSet;


public class HandleMessage {
    private ByteArrayOutputStream byteStream;

    public HandleMessage() {}

    // Generate message given the message type and a string
    public byte[] generateMsg(int msgType, String msg) throws IOException {
        byteStream = new ByteArrayOutputStream();


        if (msgType == 5){
            BitSet bitField = new BitSet(8);

            String filePath = "../../../resources/PeerInfo.cfg";
            Map<Integer, Integer> hasFile = new HashMap<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
                String row;
                while ((row = reader.readLine()) != null){
                    String[] columns = row.split("\\s+");
                    int peerID = Integer.parseInt(columns[0]);
                    int val = Integer.parseInt(columns[columns.length - 1]);

                    hasFile.put(peerID, val);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            for (Map.Entry<Integer, Integer> entry : hasFile.entrySet()){
                int key = entry.getKey();
                int value = entry.getValue();
                if (key == Integer.parseInt(msg)){
                    if (value == 1){
                    bitField.set(0,8);
                    }
                } 
            }
            byte[] msgBytes = bitField.toByteArray();
            byte[] msgLength = ByteBuffer.allocate(4).putInt(msgBytes.length).array();
            byteStream.write(msgLength);
            byteStream.write((byte)msgType);
            byteStream.write(msgBytes);

            System.out.println(byteStream);
            return byteStream.toByteArray();

            

        }
        // Write the header
        byte[] msgBytes = msg.getBytes();
        byte[] msgLength = ByteBuffer.allocate(4).putInt(msgBytes.length).array();
        byteStream.write(msgLength);
        byteStream.write((byte)msgType);
        byteStream.write(msgBytes);

        return byteStream.toByteArray();
    }

    // Removes the header portion of a received message
    public byte[] getMsg(byte[] msg) throws IOException{
        if(msg.length < 5) {
            throw new IOException("Missing message header information");
        }

        // Extract header length and return actual message portion
        byte[] msgLengthBytes = Arrays.copyOfRange(msg,0,4);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();

        return Arrays.copyOfRange(msg,5,5 + msgLength);
    }
}
