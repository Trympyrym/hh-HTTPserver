package trympyrymHTTPserver.FileServer;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Created by Trympyrym on 30.01.2017.
 */
public class CacheTask implements Callable<ByteBuffer> {

    private final Path filepath;

    public CacheTask(Path filepath) {
        this.filepath = filepath;
    }

    @Override
    public ByteBuffer call() throws Exception {
        FileChannel channel = FileChannel.open(filepath);
        int size = (int)channel.size();
        ByteBuffer result = ByteBuffer.allocateDirect(size);
        int nRead;
        while ((nRead = channel.read(result)) != size)
        {
            if (nRead != 0)
            {
                System.out.println(nRead);
            }
        }
        return result;
    }
}
