package trympyrymHTTPserver.HTTPserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trympyrym on 30.01.2017.
 */
public enum MIMEType {
    TEXT_HTML{
        @Override
        public String toString()
        {
            return "text/html";
        }
    },
    APPLICATION_JAVASCRIPT{
        @Override
        public String toString()
        {
            return "application/javascript";
        }
    },
    IMAGE_JPEG{
        @Override
        public String toString()
        {
            return "image/jpeg";
        }
    },
    TEXT_PLAIN{
        @Override
        public String toString()
        {
            return "text/plain";
        }
    };

    private static Map<String, MIMEType> allowedExtensions = new HashMap<>();

    static
    {
        allowedExtensions.put("htm", MIMEType.TEXT_HTML);
        allowedExtensions.put("html", MIMEType.TEXT_HTML);
        allowedExtensions.put("htmls", MIMEType.TEXT_HTML);
        allowedExtensions.put("shtml", MIMEType.TEXT_HTML);
        allowedExtensions.put("js", MIMEType.APPLICATION_JAVASCRIPT);
        allowedExtensions.put("jfif", MIMEType.IMAGE_JPEG);
        allowedExtensions.put("jfif-tbnl", MIMEType.IMAGE_JPEG);
        allowedExtensions.put("jpe", MIMEType.IMAGE_JPEG);
        allowedExtensions.put("jpeg", MIMEType.IMAGE_JPEG);
        allowedExtensions.put("jpg", MIMEType.IMAGE_JPEG);
        allowedExtensions.put("txt", MIMEType.TEXT_PLAIN);
    }

    public static MIMEType getByExtension(String extension)
    {
        return (allowedExtensions.containsKey(extension)) ? allowedExtensions.get(extension) : null;
    }
}
