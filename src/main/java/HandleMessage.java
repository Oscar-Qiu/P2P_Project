import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandleMessage {
    private ByteArrayOutputStream byteStream;

    public HandleMessage() {}

    // Generate message given the message type and a string
    public byte[] generateMsg(int msgType, String msg) throws IOException {
        byteStream = new ByteArrayOutputStream();

        byte[] msgBytes = msg.getBytes();
        byte[] msgLength = ByteBuffer.allocate(4).putInt(msgBytes.length).array();

        // Write the header
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
