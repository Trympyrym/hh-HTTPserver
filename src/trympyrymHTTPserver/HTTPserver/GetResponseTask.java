package trympyrymHTTPserver.HTTPserver;

import trympyrymHTTPserver.FileServer.FileServer;

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
    private final FileServer fileServer;
    private HTTPRequest request;

    public GetResponseTask(SocketChannel sc, FileServer fileServer) {
        this.sc = sc;
        this.fileServer = fileServer;
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
        fileServer.transferFile(sc, request.getRequestedFilename());
    }

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
