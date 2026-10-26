import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Socketer {
    private static HashMap<String, byte[][]> directory;

    public Socketer() {
        directory = new HashMap<>();
    }

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
                            for(Chunk chunk : chunks)
                                stream.write(chunk.bytes());
                            stream.close();
                            receiver.close();
                            ss.close();
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
