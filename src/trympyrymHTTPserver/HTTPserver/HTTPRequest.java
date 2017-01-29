package trympyrymHTTPserver.HTTPserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trympyrym on 28.01.2017.
 */
public class HTTPRequest {

    private static Map<String, String> allowedFileExtendions = new HashMap<>();
    static
    {
        allowedFileExtendions.put("htm", "text/html");
        allowedFileExtendions.put("html", "text/html");
        allowedFileExtendions.put("htmls", "text/html");
        allowedFileExtendions.put("shtml", "text/html");
        allowedFileExtendions.put("js", "application/javascript");
        allowedFileExtendions.put("jfif", "image/jpeg");
        allowedFileExtendions.put("jfif-tbnl", "image/jpeg");
        allowedFileExtendions.put("jpe", "image/jpeg");
        allowedFileExtendions.put("jpeg", "image/jpeg");
        allowedFileExtendions.put("jpg", "image/jpeg");

        allowedFileExtendions.put("txt", "text/plain");
    }

    public static HTTPRequest parse(String argString)
    {
        try
        {
            String[] firstLineSplitted = argString.split("\\n")[0].split("[ ?]");
            HTTPMethod method = HTTPMethod.valueOf(firstLineSplitted[0]);
            Map<String, String> params = ConvertToMap(firstLineSplitted[2]);
            String filename = params.get("file");
            String[] filenameSplitted = filename.split("\\.");
            String extension = filenameSplitted[filenameSplitted.length - 1];
            return new HTTPRequest(method, filename, extension);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new HTTPRequest(null, null, null);
        }
    }

    private static Map ConvertToMap(String paramsAsString)
    {
        String[] paramsDivided = paramsAsString.split("&");
        Map result = new HashMap();
        for (int i = 0; i < paramsDivided.length; i++)
        {
            String[] paramNameAndValue = paramsDivided[i].split("=");
            result.put(paramNameAndValue[0], paramNameAndValue[1]);
        }
        return result;
    }

    private final HTTPMethod httpMethod;
    private final String requestedFilename;

    private final String extension;
    private final boolean valid;
    private final String mimeType;

    private HTTPRequest(HTTPMethod method, String filename, String extension)
    {
        this.httpMethod = method;
        this.requestedFilename = filename;
        this.extension = extension;
        this.valid = (method != null) && (filename != null) && (extension != null)
                && HTTPRequest.allowedFileExtendions.containsKey(extension);
        this.mimeType = (this.valid) ? (HTTPRequest.allowedFileExtendions.get(extension)) : (null);
    }


    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestedFilename() {
        return requestedFilename;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMimeType() {
        return mimeType;
    }

}
