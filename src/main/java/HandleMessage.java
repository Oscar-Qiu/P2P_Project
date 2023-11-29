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

    // Generate a message string given a string (used for testing, message type is set as 8)
    public byte[] genStrMsg(String msg) throws IOException {
        byteStream = new ByteArrayOutputStream();

        // Write the header
        byte[] msgBytes = msg.getBytes();
        byte[] msgLength = ByteBuffer.allocate(4).putInt(msgBytes.length).array();
        byteStream.write(msgLength);
        byteStream.write((byte)8);
        byteStream.write(msgBytes);

        return byteStream.toByteArray();
    }

    // Generate bitField message given the peer's bitField
    public byte[] genBFMsg(boolean[] bitField) throws IOException {
        byteStream = new ByteArrayOutputStream();

        // create the byte array from the bitField
        byte[] msgBytes = new byte[bitField.length / 8];
        for (int entry = 0; entry < msgBytes.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (bitField[entry * 8 + bit]) {
                    msgBytes[entry] |= (128 >> bit);
                }
            }
        }

        byte[] msgLength = ByteBuffer.allocate(4).putInt(msgBytes.length).array();
        byteStream.write(msgLength);
        byteStream.write((byte)5);
        byteStream.write(msgBytes);

        return byteStream.toByteArray();
    }

    // Generate interested message
    public byte[] genIntMsg() throws IOException {
        byteStream = new ByteArrayOutputStream();

        byte[] msgLength = {0, 0, 0, 0}; // length is zero
        byteStream.write(msgLength);
        byteStream.write((byte)2);

        return byteStream.toByteArray();
    }

    // Generate not interested message
    public byte[] genNotIntMsg() throws IOException {
        byteStream = new ByteArrayOutputStream();

        byte[] msgLength = {0, 0, 0, 0}; // length is zero
        byteStream.write(msgLength);
        byteStream.write((byte)3);

        return byteStream.toByteArray();
    }

    // Removes the header portion of a received string message
    public String getMsgStr(byte[] msg) throws IOException{
        if(msg.length < 5) {
            throw new IOException("Missing message header information");
        }

        // Extract header length and return actual message portion
        byte[] msgLengthBytes = Arrays.copyOfRange(msg,0,4);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();

        return new String(Arrays.copyOfRange(msg,5,5 + msgLength));
    }

    // Updates the bifField map given a bitField message and returns a bool if the peer has a piece the current does not
    public boolean handleBFMsg(byte[] msg, Map<String,boolean[]> idToBitField, String currID, String peerID) throws IOException{
        if(msg.length < 5) {
            throw new IOException("Missing message header information");
        }

        // Extract header length and return actual message portion
        byte[] msgLengthBytes = Arrays.copyOfRange(msg,0,4);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();

        byte[] bitField = Arrays.copyOfRange(msg,5,5 + msgLength);

        // test message prints whole byte array
        System.out.print("Received bitField (as bytes): ");
        for(int i = 0; i < bitField.length; i++){
            System.out.print((bitField[0] & 0xFF) + " ");
        }

        System.out.println("");

        BitSet b = BitSet.valueOf(bitField);

        // update the bit Field of the peer
        for(int i = 0; i < idToBitField.get(peerID).length; i++) {
            idToBitField.get(peerID)[i] = b.get(i);
        }

        boolean interested = false;

        // checks to see if peer has any needed pieces
        for(int i = 0; i < idToBitField.get(currID).length; i++) {
            if(!idToBitField.get(currID)[i] && idToBitField.get(peerID)[i]){
                interested = true;
                i = idToBitField.get(currID).length;
            }
        }

        return interested;
    }

}
