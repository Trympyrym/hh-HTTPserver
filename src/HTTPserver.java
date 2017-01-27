import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by Trympyrym on 27.01.2017.
 */
public class HTTPServer {

    private static final int port = 1234;
    private ServerSocketChannel ssc;

    public HTTPServer() throws IOException {
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
