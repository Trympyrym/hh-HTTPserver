import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
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
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);

        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void mainloop() throws IOException, InterruptedException {
        while (true)
        {
            int num = selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            System.out.println("outer loop. num = " + num);

            while (iterator.hasNext())
            {
                System.out.println("inner loop");
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isAcceptable())
                {
                    SocketChannel sc = ssc.accept();
                    System.out.println("acceptable");
                    if (sc != null)
                    {
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    }
                }
                else if (selectionKey.isReadable())
                {
                    System.out.println("readable");
                    executor.submit(new GetResponseTask((SocketChannel)selectionKey.channel(), config));
                        selectionKey.interestOps(selectionKey.interestOps() ^ SelectionKey.OP_READ);
                }
            }
        }
    }
}
