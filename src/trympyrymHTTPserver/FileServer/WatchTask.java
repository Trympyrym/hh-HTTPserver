package trympyrymHTTPserver.FileServer;

import trympyrymHTTPserver.Config;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by Trympyrym on 30.01.2017.
 */
public class WatchTask implements Runnable {
    private final Config config;
    private final Map<Path, ByteBuffer[]> cachedFiles;
    private final ExecutorService executor;

    public WatchTask(ExecutorService executor, Config config, Map<Path, ByteBuffer[]> cachedFiles) {
        this.config = config;
        this.cachedFiles = cachedFiles;
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            Path directory = Paths.get(config.getDirectory());
            WatchService watcher = directory.getFileSystem().newWatchService();
            directory.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true)
            {
                WatchKey watchKey = watcher.take();
                for (WatchEvent event : watchKey.pollEvents())
                {
                    WatchEvent.Kind kind = event.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        executor.submit(new CacheTask(getPath((Path)event.context()), config, cachedFiles));
                    }
                    if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        synchronized (cachedFiles)
                        {
                            if (cachedFiles.containsKey(getPath((Path)event.context())))
                            {
                                cachedFiles.remove(getPath((Path)event.context()));
                            }
                        }
                    }
                }
                watchKey.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Path getPath(Path wrongPath)
    {
        //Play more this soft French tambourine so drink tea

        String wrongPathAsString = wrongPath.toString();
        String wrongAbsolutePathAsString = wrongPath.toAbsolutePath().toString();
        String wrongDirectory = wrongAbsolutePathAsString.substring(0,
                wrongAbsolutePathAsString.lastIndexOf(wrongPathAsString));
        return Paths.get(config.getDirectory() + File.separator + wrongPathAsString);
    }
}
