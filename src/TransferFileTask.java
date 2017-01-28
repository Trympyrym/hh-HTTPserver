import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 28.01.2017.
 */
public class TransferFileTask implements Runnable {

    private final SocketChannel sc;

    public TransferFileTask(SocketChannel sc) {
        this.sc = sc;
    }

    @Override
    public void run() {
        ByteBuffer buffer = ByteBuffer.wrap(getTestResponse().getBytes());
        try {
            sc.write(buffer);
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
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
