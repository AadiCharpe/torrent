import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

record Chunk(byte[] bytes) {};

public class FileChunker {
    public static final int CHUNK_SIZE = 256 * 1024;

    protected static Chunk[] chunk(File file) throws IOException {
        ArrayList<Chunk> chunksBuffer = new ArrayList<>();

        FileInputStream fis = new FileInputStream(file);
        while(fis.available() > 0) {
            byte[] read = fis.readNBytes(CHUNK_SIZE);
            chunksBuffer.add(new Chunk(read));
        }
        fis.close();

        return chunksBuffer.toArray(new Chunk[0]);
    }
}