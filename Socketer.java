import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Socketer {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8189);
            while(true) {
                Socket receiver = ss.accept();
                new Thread() {
                    public void run() {
                        File file = new File("battle-log.png");
                        Chunk[] chunks = new Chunk[0];
                        try {
                            chunks = FileChunker.chunk(file);
                            OutputStream stream = receiver.getOutputStream();

                            Chunk[] hashedChunks;
                            hashedChunks = Hasher.hash_file(chunks);

                            Manifest m = new Manifest(file.getName(), file.length(), FileChunker.CHUNK_SIZE, FileChunker.chunkArrToByte2DArr(hashedChunks));
                            System.out.println(m);

                            stream.write(m.toBytes());
                            for(Chunk chunk : chunks)
                                stream.write(chunk.bytes());

                            stream.close();
                            receiver.close();
                        } catch(Exception e) {
                            System.err.println(e);
                        }
                    }
                }.start();
            }
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}