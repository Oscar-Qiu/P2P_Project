import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HandShake {
    private static final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private static final int ZERO_BITS_LENGTH = 10; // 10-byte zero bits
    private static final int PEER_ID_SIZE = 4;      // 4-byte peer ID
    private static final int HANDSHAKE_LENGTH = 32; // Total handshake message length
    private ByteArrayOutputStream byteStream;

    public HandShake() {}

    // Generate handshake
    public byte[] generateHandShakeMsg(int peerID) throws IOException {
        /* byteStream = new ByteArrayOutputStream();

        // Write the handshake header
        byteStream.write(HANDSHAKE_HEADER.getBytes());

        // Write the 10 byte zero bits
        byteStream.write(new byte[ZERO_BITS_LENGTH]);

        // Write 4-byte peerID
        byte[] peerIDBytes = ByteBuffer.allocate(4).putInt(peerID).array();
        byteStream.write(peerIDBytes);

        //return byteStream.toByteArray();
        System.out.println(byteStream.toByteArray());
        return byteStream.toByteArray(); */

        System.out.println("----------------------------------------");
        System.out.println("Generating handshake...");

        // Set handshake header
        byte[] handshakeMessage = new byte[32];
        byte[] headerBytes = HANDSHAKE_HEADER.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(headerBytes, 0, handshakeMessage, 0, headerBytes.length);

        // Set zero bytes
        for (int i = 18; i < 28; i++) {
            handshakeMessage[i] = 0;
        }
        System.out.println(handshakeMessage);

        // Set peer id bytes
        byte[] peerIDBytes = ByteBuffer.allocate(4).putInt(peerID).array();
        System.arraycopy(peerIDBytes, 0, handshakeMessage, 28, peerIDBytes.length);
        System.out.println(handshakeMessage);

        System.out.println("Handshake message to send: " + new String(handshakeMessage));
        System.out.println("----------------------------------------");
        return handshakeMessage;
    }

    // Parse an incoming handshake and return the received peer's id
    public int parseHandShakeMsg(byte[] receivedMsg) throws IOException {
        if(receivedMsg.length != HANDSHAKE_LENGTH) {
            throw new IOException("Incorrect handshake message length");
        }

        // Extract header message by its portion
        byte[] headerBytes = Arrays.copyOfRange(receivedMsg,0,18);
        byte[] zeroBytes = Arrays.copyOfRange(receivedMsg,18,28);
        byte[] peerIDBytes = Arrays.copyOfRange(receivedMsg,28,32);

        // Reconstruct the header
        String header = new String(headerBytes);
        if(!HANDSHAKE_HEADER.equals(header)) {
            throw new IOException("Invalid handshake header");
        }

        // Reconstruct zero bits
        byte[] expectedZeroBits = new byte[ZERO_BITS_LENGTH];
        if(!Arrays.equals(expectedZeroBits, zeroBytes)) {
            throw new IOException("Invalid zero bits section");
        }

        int receivedPeerID = ByteBuffer.wrap(peerIDBytes).getInt();
        return receivedPeerID;
    }
}
