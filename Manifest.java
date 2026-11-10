import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public record Manifest (String name, long size, int chunkSize, byte[][] hashes) {
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);

        dout.writeUTF(name);
        dout.writeLong(size);
        dout.writeInt(chunkSize);
        dout.writeInt(hashes.length);

        for(byte[] hash : hashes)
            dout.write(hash);
        dout.close();

        return bout.toByteArray();
    }
    public static Manifest fromBytes(byte[] bytes) throws IOException {
        return read(new DataInputStream(new ByteArrayInputStream(bytes)));
    }

    public String toString() {
        return 
            String.format(
                "Manifest %s: file is %s bytes, each chunk is %s bytes, %s chunks", 
                name,
                size,
                chunkSize,
                hashes.length
            )
        ;
    }
    
    public static Manifest read(DataInputStream in) throws IOException{
        String name = in.readUTF();
        long size = in.readLong();
        int chunkSize = in.readInt();
        int length = in.readInt();
        byte[][] hashes = new byte[length][];

        for(int i = 0; i < length; i++)
            hashes[i] = in.readNBytes(32);

        return new Manifest(name, size, chunkSize, hashes);
    }
}