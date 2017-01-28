import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 28.01.2017.
 */
public class GetResponseTask implements Runnable {

    private final SocketChannel sc;
    private HTTPRequest request;

    public GetResponseTask(SocketChannel sc) {
        this.sc = sc;
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
        ByteBuffer buffer = ByteBuffer.wrap(getResponse().getBytes());
        try {

            sc.write(buffer);
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponse()
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

        result = result + "Hello, world!\n";
        result = result + "Method: " + request.getHttpMethod() +"\n";
        result = result + "Requested file: " + request.getRequestedFile() +"\n";

        return result;
    }
}
