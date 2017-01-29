import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class HTTPServer {

    private final ServerSocketChannel ssc;
    private final int port;
    private final String directory;
    private final Map<String, Set<FileOption>> fileOptions;
    private final Selector selector = Selector.open();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Config config;

    public HTTPServer(String configFilename) throws IOException {
        config = new Config(configFilename);
        config.read();
        directory = config.getDirectory();
        port = config.getPort();
        fileOptions = config.getFileOptions();

        ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);

        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void mainloop() throws IOException, InterruptedException {
        while (true)
        {
            int num = selector.select();

            if (num == 0)
            {
                continue;
            }


            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            for (SelectionKey selectionKey : selectionKeys)
            {
                if (selectionKey.isAcceptable())
                {
                    SocketChannel sc = ssc.accept();

                    if (sc != null)
                    {
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    }
                }
                else if (selectionKey.isReadable())
                {
                    executor.submit(new GetResponseTask((SocketChannel)selectionKey.channel(), config));
                }
            }
        }
    }
}
