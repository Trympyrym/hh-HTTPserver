package trympyrymHTTPserver.FileServer;

import trympyrymHTTPserver.Config;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by Trympyrym on 29.01.2017.
 */
public class FileServer {
    private final ExecutorService executor;
    private final Map<String, Set<FileOption>> fileOptions;
    private final Config config;
    private Map<Path, ByteBuffer[]> cachedFiles = new HashMap<>();

    public FileServer(Config config, ExecutorService executorService) {
        this.config = config;

        this.executor = executorService;

        this.fileOptions = config.getFileOptions();
    }

    public void start() throws IOException, ExecutionException, InterruptedException {
        cacheFiles();
        Thread watchThread = new Thread(new WatchTask(executor, config, cachedFiles));
        watchThread.start();
    }


    public int checkFile(String filename)
    {
        synchronized (cachedFiles)
        {
            return  (cachedFiles.containsKey(getPath(filename))) ? 0 : 404;
        }
//        Path path = getPath(filename);
//        if (!Files.exists(path) || !Files.isRegularFile(path))
//        {
//            return 404;
//        }
//        if (!Files.isReadable(path))
//        {
//            return 403;
//        }
//        return 0;
    }
    public void transferFile(SocketChannel channelTo, String filename) throws IOException {
        ByteBuffer[] cachedFile;
        synchronized (cachedFiles)
        {
            cachedFile = cachedFiles.get(getPath(filename));
        }
        for (int i = 0; i < cachedFile.length; i++)
        {
            ByteBuffer buffer;
            synchronized (cachedFile)
            {
                buffer = cachedFile[i];
            }
            synchronized (buffer)
            {
                buffer.rewind();
                while (buffer.hasRemaining())
                {
                    channelTo.write(buffer);
                }
            }
        }
        channelTo.close();
//        FileChannel channel = FileChannel.open(getPath(filename));
//        long size = channel.size();
//        long transferred = channel.transferTo(0, size, channelTo);
//
//        while (transferred < size)
//        {
//            transferred += channel.transferTo(transferred, size - transferred, channelTo);
//        }
//        channel.close();
//        channelTo.close();
    }

    private Path getPath(String filename)
    {
        return Paths.get(config.getDirectory() + File.separator + filename);
    }

    private void cacheFiles() throws IOException, ExecutionException, InterruptedException {
        List<Path> filepaths = Files.walk(Paths.get(config.getDirectory()))
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        Map<Path, Future> futures = new HashMap<>();
        for (Path filepath : filepaths)
        {
            futures.put(filepath, executor.submit(
                    new CacheTask(filepath, config, cachedFiles)));
        }
        int counter = 0;
        int size = futures.size();

        while (counter < size)
        {
            counter = 0;
            for (Map.Entry<Path, Future> entry : futures.entrySet())
            {
                counter += entry.getValue().isDone() ? 1 : 0;
            }
        }
    }
}
