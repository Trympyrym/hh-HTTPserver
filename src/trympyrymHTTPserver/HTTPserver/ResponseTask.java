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
public class ResponseTask implements Runnable {

    private final SocketChannel sc;
    private final FileServer fileServer;
    private HTTPRequest request;

    public ResponseTask(SocketChannel sc, FileServer fileServer) {
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
        System.out.println("incoming message is valid: " + request.isValid());
        if (!request.isValid())
        {
            try {
                returnErrorPage(Error.BAD_REQUEST);
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (request.getHttpMethod() != HTTPMethod.GET)
        {
            try {
                returnErrorPage(Error.METHOD_NOT_ALLOWED);
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            transferFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transferFile() throws IOException {
        String filename = request.getRequestedFilename();
        int errCode = fileServer.checkFile(filename);
        if (errCode != 0)
        {
            returnErrorPage(Error.NOT_FOUND);
            sc.close();
        }
        sendString(getHeader());
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
                + "Server: TrympyrymHTTPServer\n"
                + "Pragma: no-cache\n\n";

        return result;
    }

    private void returnErrorPage(Error error) throws IOException {
        sendString(error.getResponse());
    }

    private void sendString(String argString) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(argString.getBytes());
        buffer.rewind();
        while (buffer.hasRemaining())
        {
            sc.write(buffer);
        }
    }
}
