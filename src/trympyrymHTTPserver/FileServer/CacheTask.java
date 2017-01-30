package trympyrymHTTPserver.FileServer;

import trympyrymHTTPserver.Config;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Integer.max;
import static java.lang.Integer.min;


/**
 * Created by Trympyrym on 30.01.2017.
 */
public class CacheTask implements Callable<ConcurrentLinkedQueue<ByteBuffer>> {

    private final Path filepath;
    private final int nThreads;
    private final int minCacheBlock;

    public CacheTask(Path filepath, Config config) {
        this.filepath = filepath;
        this.nThreads = config.getNThreads() - 2;  // 2 threads are reserved
        this.minCacheBlock = config.getMinCacheBlock();
    }

    @Override
    public ConcurrentLinkedQueue<ByteBuffer> call() throws Exception {
        FileChannel channel = FileChannel.open(filepath);
        long size = channel.size();
        ConcurrentLinkedQueue<ByteBuffer> result = new ConcurrentLinkedQueue<>();
        int numberOfPieces = max(nThreads, 1 + (int)(size / (long)Integer.MAX_VALUE));
        numberOfPieces = min(numberOfPieces, 1 + (int)(size / (long)minCacheBlock) );

        long currentOffset = 0;
        long nextOffset = size / (long)numberOfPieces;
        for (int i = 0; i < numberOfPieces; i++)
        {
            int currentSize = (int)(nextOffset - currentOffset);
            result.add(channel.map(FileChannel.MapMode.READ_ONLY, currentOffset, currentSize));
            currentOffset = nextOffset;
            nextOffset = (i + 2)*size / (long)numberOfPieces;
        }
        return result;
    }
}
