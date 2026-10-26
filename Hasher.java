import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;

public class Hasher {
    public static byte[] hash_chunk(Chunk chunk) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(chunk.bytes());
    }

    public static Chunk[] hash_file(Chunk[] chunks) throws NoSuchAlgorithmException {
        ArrayList<Chunk> buffer = new ArrayList<>();

        for(Chunk chunk : chunks)
            buffer.add(new Chunk(hash_chunk(chunk)));

        return buffer.toArray(new Chunk[0]);
    }

    public static void main(String[] args) {
        try {
            Chunk[] chunks = hash_file(FileChunker.chunk(new File("battle-log.png")));

            HexFormat hex = HexFormat.of();
            for(int i = 0; i < chunks.length; i++)
                System.out.println("Chunk " + i + ": " + hex.formatHex(chunks[i].bytes()));
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}