package trympyrymHTTPserver.FileServer;

import trympyrymHTTPserver.Config;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by Trympyrym on 29.01.2017.
 */
public class FileServer {
    private final ExecutorService executorService;
    private final Map<String, Set<FileOption>> fileOptions;
    private final String directory;

    public FileServer(Config config, ExecutorService executorService) {
        this.directory = config.getDirectory();

        this.executorService = executorService;

        this.fileOptions = config.getFileOptions();
    }

    public int checkFile(String filename)
    {
        Path path = Paths.get(directory + File.separator + filename);
        if (!Files.exists(path) || !Files.isRegularFile(path))
        {
            return 404;
        }
        if (!Files.isReadable(path))
        {
            return 403;
        }
        return 0;
    }
    public void transferFile(SocketChannel channelTo, String filename) throws IOException {
        FileChannel channel = FileChannel.open(Paths.get(directory + File.separator + filename));
        long size = channel.size();
        long transferred = channel.transferTo(0, size, channelTo);

        while (transferred < size)
        {
            transferred += channel.transferTo(transferred, size - transferred, channelTo);
        }
        channel.close();
        channelTo.close();
    }
}
