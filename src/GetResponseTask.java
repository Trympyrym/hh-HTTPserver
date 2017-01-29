import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 28.01.2017.
 */
public class GetResponseTask implements Runnable {

    private final SocketChannel sc;
    private final Config config;
    private HTTPRequest request;

    public GetResponseTask(SocketChannel sc, Config config) {
        this.sc = sc;
        this.config = config;
    }

    @Override
    public void run() {
        ByteBuffer readBuffer = ByteBuffer.allocate(1000);
        try {
            sc.read(readBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        request = HTTPRequest.parse(new String(readBuffer.array()));

        try {
            transferFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transferFile() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(getHeader().getBytes());
        buffer.rewind();
        while (buffer.hasRemaining())
        {
            sc.write(buffer);
        }
        FileChannel channel = new FileInputStream(config.getDirectory() +
                File.separator + request.getRequestedFile()).getChannel();
        long size = channel.size();
        long transferred = channel.transferTo(0, size, sc);

        while (transferred < size)
        {
            transferred += channel.transferTo(transferred, size - transferred, sc);
        }
        channel.close();
        sc.close();
    }

    // I remember about it when I'm ready to return 4XX codes
    private String getHeader()
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

        return result;
    }
}
