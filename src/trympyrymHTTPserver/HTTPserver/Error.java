package trympyrymHTTPserver.HTTPserver;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 29.01.2017.
 */
public enum Error {
    METHOD_NOT_ALLOWED {
        @Override
        String getStatus() {
            return "405 Method not allowed";
        }

        @Override
        String getBody() {
            return "Only GET requests supported.";
        }
    },
    NOT_FOUND{
        @Override
        String getStatus() {
            return "404 Not found";
        }

        @Override
        String getBody() {
            return "File not found.";
        }
    },
    BAD_REQUEST{
        @Override
        String getStatus() {
            return "400 Bad request";
        }

        @Override
        String getBody() {
            return "Cant parse your request or some params are invalid.";
        }
    };

    //dunno how to make private
    abstract String getStatus();
    abstract String getBody();
    public String getResponse()
    {
        String result = "HTTP/1.1 " + getStatus() + "\n";

        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        result = result + "Date: " + df.format(new Date()) + "\n";

        result = result
                + "Content-Type: text/html\n"
                + "Connection: close\n"
                + "Server: TrympyrymHTTPServer\n"
                + "Pragma: no-cache\n\n";

        return result + getBody();
    };
}
