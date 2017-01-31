package trympyrymHTTPserver.FileServer;

import trympyrymHTTPserver.Config;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.Map;

import static java.lang.Integer.max;
import static java.lang.Integer.min;


/**
 * Created by Trympyrym on 30.01.2017.
 */
public class CacheTask implements Runnable {

    private final Path filepath;
    private final int nThreads;
    private final int minCacheBlock;
    private Map<Path, ByteBuffer[]> cachedFiles;

    public CacheTask(Path filepath, Config config, Map<Path, ByteBuffer[]> cachedFiles) {
        this.cachedFiles = cachedFiles;
        this.filepath = filepath;
        this.nThreads = config.getNThreads() - 2;  // 2 threads are reserved
        this.minCacheBlock = config.getMinCacheBlock();
    }



    @Override
    public void run(){
        FileChannel channel = null;
        FileLock lock = null;
        try {
            channel = FileChannel.open(filepath);
            lock = channel.lock();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long size = 0;
        try {
            size = channel.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int numberOfPieces = max(nThreads, 1 + (int)(size / (long)Integer.MAX_VALUE));
        numberOfPieces = min(numberOfPieces, 1 + (int)(size / (long)minCacheBlock) );

        long currentOffset = 0;
        long nextOffset = size / (long)numberOfPieces;
        ByteBuffer[] buffers = new ByteBuffer[numberOfPieces];

        for (int i = 0; i < numberOfPieces; i++)
        {
            int currentSize = (int)(nextOffset - currentOffset);
            buffers[i] = ByteBuffer.allocateDirect(currentSize);
            currentOffset = nextOffset;
            nextOffset = (i + 2)*size / (long)numberOfPieces;
        }

        int nRead = 0;

        try {
            while (nRead < channel.size())
            {
                nRead += channel.read(buffers);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
            lock.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
        synchronized (cachedFiles)
        {
            cachedFiles.put(filepath, buffers);
        }
    }
}
