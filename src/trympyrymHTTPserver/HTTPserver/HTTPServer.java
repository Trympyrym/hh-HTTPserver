package trympyrymHTTPserver.HTTPserver;

import trympyrymHTTPserver.Config;
import trympyrymHTTPserver.FileServer.FileServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class HTTPServer {

    private final ServerSocketChannel ssc;
    private final int port;
    private final Selector selector = Selector.open();
    private final ExecutorService executor;
    private final Config config;
    private final FileServer fileServer;

    public HTTPServer(Config config, ExecutorService executor, FileServer fileServer) throws IOException {
        this.config = config;
        this.executor = executor;
        this.fileServer = fileServer;

        port = config.getPort();

        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);

        ssc.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void mainloop() throws IOException, InterruptedException {
        while (selector.select() > -1)
        {
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext())
            {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
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
                    executor.submit(new ResponseTask((SocketChannel)selectionKey.channel(), fileServer));
                        selectionKey.interestOps(selectionKey.interestOps() ^ SelectionKey.OP_READ);
                }
            }
        }
    }
}
