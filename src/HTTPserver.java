import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class HTTPServer {

    private final ServerSocketChannel ssc;
    private final int port;
    private final String directory;
    private final Map<String, Set<FileOption>> fileOptions;
    private final Selector selector = Selector.open();

    public HTTPServer(String configFilename) throws IOException {
        Config config = new Config(configFilename);
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
                    SocketChannel sc = (SocketChannel)selectionKey.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(500);
                    sc.read(buffer);
                    System.out.println(new String(buffer.array()).trim());
                    buffer.flip();
                    buffer = ByteBuffer.wrap(getTestResponse().getBytes());
                    sc.write(buffer);
                    sc.close();
                }
//                else if (selectionKey.isWritable())
//                {
//                    SocketChannel sc = ssc.accept();
//                    (So
//                }
            }


//            System.out.println("Waiting for connections");
//            SocketChannel sc = ssc.accept();
//            if (sc == null)
//            {
//                TimeUnit.SECONDS.sleep(2);
//            }
//            else
//            {
//                System.out.println("blah-blah");
//                System.out.println(getTestResponse());
//                ByteBuffer buffer = ByteBuffer.wrap(getTestResponse().getBytes());
//                buffer.rewind();
//                sc.write(buffer);
//                sc.close();
//            }
        }
    }


    private String getTestResponse()
    {
        String result = "HTTP/1.1 200 OK\n";

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        result = result + "Date: " + df.format(new Date()) + "\n";

        result = result
                + "Content-Type: text/plain\n"
                + "Connection: close\n"
                + "Server: SimpleWEBServer\n"
                + "Pragma: no-cache\n\n";

        result = result + "Hello, world!";

        return result;
    }
}
