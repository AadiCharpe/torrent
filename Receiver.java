import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Receiver {
    public static final int CHUNK_SIZE = 256 * 1024;

    public static File receive(Chunk[] chunks, String name) throws IOException {
        File f = new File(name);
        FileOutputStream fout = new FileOutputStream(f);

        for (Chunk chunk : chunks) {
            System.out.println(chunk.bytes().length);
            fout.write(chunk.bytes());
        }
        fout.close();

        //return addExtension(f);
        return f;
    }

    private static File addExtension(File f) throws IOException {
        Path path = f.toPath();
        String mimeType = probeMimeType(path);
        if (mimeType == null) {
            return f;
        }

        String extension = switch (mimeType) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/gif" -> "gif";
            case "application/pdf" -> "pdf";
            case "application/zip" -> "zip";
            case "text/plain" -> "txt";
            default -> mimeType.substring(mimeType.indexOf('/') + 1);
        };

        File renamed = new File(f.getParentFile(), f.getName() + "." + extension);
        Files.move(path, renamed.toPath());
        return renamed;
    }

    private static String probeMimeType(Path path) throws IOException {
        Process process = new ProcessBuilder("file", "--mime-type", "-b", path.toString())
                .redirectErrorStream(true)
                .start();

        String output;
        try (var in = process.getInputStream()) {
            output = new String(in.readAllBytes()).trim();
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return process.exitValue() == 0 && !output.isEmpty() ? output : null;
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
            InputStream in = socket.getInputStream();

            DataInputStream din = new DataInputStream(in);
            Manifest m = Manifest.read(din);
            System.out.println(m);

            ArrayList<Chunk> chunksBuffer = new ArrayList<>();

            
            for (int i = 0; i < m.hashes().length; i++)
                chunksBuffer.add(new Chunk(din.readNBytes(CHUNK_SIZE)));
            din.close();
            socket.close();

            System.out.println("buff size: " + chunksBuffer.size());

            receive(chunksBuffer.toArray(new Chunk[0]), args[2]);
        } catch(Exception e) {
            System.err.println(e);
        }
    }
}