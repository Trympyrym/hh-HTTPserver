import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

public class HTTPserver {

    private static final int port = 1234;

    public static void main(String[] args) throws IOException, InterruptedException {
	// write your code here
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.configureBlocking(false);

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
