import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Receiver {
    public static final int CHUNK_SIZE = 256 * 1024;

    public static File receive(Chunk[] chunks) throws IOException {
        File f = new File("output");
        FileOutputStream fout = new FileOutputStream(f);

        for (Chunk chunk : chunks) {
            fout.write(chunk.bytes());
        }
        fout.close();

        return addExtension(f);
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
        File file = new File("battle-log.png");
        Chunk[] chunks = new Chunk[0];
        try {
            chunks = FileChunker.chunk(file);
            receive(chunks);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}