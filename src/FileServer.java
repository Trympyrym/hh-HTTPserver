import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * Created by Trympyrym on 29.01.2017.
 */
public class FileServer {
    private final Config config;
    private final ExecutorService executorService;

    public FileServer(Config config, ExecutorService executorService) {
        this.config = config;
        this.executorService = executorService;
    }

    public void transferFile(SocketChannel channelTo, String filename) throws IOException {
        FileChannel channel = new FileInputStream(config.getDirectory() +
                File.separator + filename).getChannel();
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
