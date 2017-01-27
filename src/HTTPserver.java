import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class HTTPServer {

    private final ServerSocketChannel ssc;
    private final int port;
    private final String directory;
    private final Map<String, Set<FileOption>> fileOptions;

    public HTTPServer(String configFilename) throws IOException {
        Config config = new Config(configFilename);
        config.read();
        directory = config.getDirectory();
        port = config.getPort();
        fileOptions = config.getFileOptions();
        ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);
    }

    public void mainloop() throws IOException, InterruptedException {
        while (true)
        {
            System.out.println("Waiting for connections");
            SocketChannel sc = ssc.accept();
            if (sc == null)
            {
                TimeUnit.SECONDS.sleep(2);
            }
            else
            {
                System.out.println("Incoming connection from: " + sc.socket().getRemoteSocketAddress());
                sc.close();
            }
        }
    }


}
