package trympyrymHTTPserver.HTTPserver;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Trympyrym on 29.01.2017.
 */
public enum Error {
    METHOD_NOT_ALLOWED {
        public String getResponse()
        {
            String result = "HTTP/1.1 405 Method not allowed\n";

            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            result = result + "Date: " + df.format(new Date()) + "\n";

            result = result
                    + "Content-Type: text/html\n"
                    + "Connection: close\n"
                    + "Server: TrympyrymHTTPServer\n"
                    + "Pragma: no-cache\n\n";
            return result + "Only GET requests supported.";
        }
    },
    NOT_FOUND{
        public String getResponse()
        {
            String result = "HTTP/1.1 404 Not found\n";

            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            result = result + "Date: " + df.format(new Date()) + "\n";

            result = result
                    + "Content-Type: text/html\n"
                    + "Connection: close\n"
                    + "Server: TrympyrymHTTPServer\n"
                    + "Pragma: no-cache\n\n";

            return result + "File not found.";
        }
    },
    BAD_REQUEST{
        public String getResponse()
        {
            String result = "HTTP/1.1 400 Bad request\n";

            DateFormat df = DateFormat.getTimeInstance();
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            result = result + "Date: " + df.format(new Date()) + "\n";

            result = result
                    + "Content-Type: text/html\n"
                    + "Connection: close\n"
                    + "Server: TrympyrymHTTPServer\n"
                    + "Pragma: no-cache\n\n";

            return result + "Cant parse your request or some params are invalid.";
        }
    };

    public abstract String getResponse();
}
