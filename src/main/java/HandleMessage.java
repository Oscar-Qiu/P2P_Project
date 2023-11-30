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
    public byte[] genIMsg() throws IOException{
        byteStream = new ByteArrayOutputStream();
        
        // Write the header
        byteStream.write((byte)4);
        byteStream.write((byte)2);

        return byteStream.toByteArray();


    }

    public byte[] genNIMsg() throws IOException{
        byteStream = new ByteArrayOutputStream();
        
        // Write the header
        byteStream.write((byte)4);
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

    // Removes the header portion of a received bitField message
    public byte[] getMsgBF(byte[] msg) throws IOException{
        if(msg.length < 5) {
            throw new IOException("Missing message header information");
        }

        // Extract header length and return actual message portion
        byte[] msgLengthBytes = Arrays.copyOfRange(msg,0,4);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();

        byte[] bf = Arrays.copyOfRange(msg,5,5 + msgLength);

        // test message prints whole byte array
        // System.out.print("Received bitField (as bytes): ");
        /*for(int i = 0; i < bf.length; i++){
            System.out.print((bf[i] & 0xFF) + " ");
        }
        System.out.print("\n");*/

        return bf;
    }
}
