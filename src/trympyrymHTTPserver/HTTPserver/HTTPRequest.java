package trympyrymHTTPserver.HTTPserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trympyrym on 28.01.2017.
 */
public class HTTPRequest {

    public static HTTPRequest parse(String argString)
    {
        String[] firstLineSplitted = argString.split("\\n")[0].split("[ ?]");
        HTTPMethod method = HTTPMethod.valueOf(firstLineSplitted[0]);
        String paramsAsString = firstLineSplitted[2];
        return new HTTPRequest(method, ConvertToMap(paramsAsString));
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

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestedFilename() {
        return requestedFile;
    }

    private HTTPMethod httpMethod;
    private String requestedFile;

    private HTTPRequest(HTTPMethod method, Map<String, String> params)
    {
        this.httpMethod = method;
        this.requestedFile = params.get("file");
    }
}
