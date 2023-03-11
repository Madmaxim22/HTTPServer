import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEW_LINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";
    private final String message;
    private final HTTPMethod method;
    private final URI url;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private final List<NameValuePair> queryParams;

    public Request(String message) {
        this.message = message;
        String[] parts = message.split(DELIMITER);
        String head = parts[0];
        String[] headers = head.split(NEW_LINE);
        String[] firstLine = headers[0].split(" ");
        method = HTTPMethod.valueOf(firstLine[0]);
        url = URI.create(firstLine[1]);
        URIBuilder builder = new URIBuilder(url, StandardCharsets.UTF_8);
        queryParams = builder.getQueryParams();
        path = builder.getPath();
        this.headers = Collections.unmodifiableMap(
                new HashMap<>() {{
                    for (int i = 1; i < headers.length; i++) {
                        String[] headerPart = headers[i].split(HEADER_DELIMITER, 2);
                        put(headerPart[0].trim(), headerPart[1].trim());
                    }
                }}
        );
        String bodyLength = this.headers.get("Content-Length");
        int length = bodyLength != null ? Integer.parseInt(bodyLength) : 0;
        this.body = parts.length > 1 ? parts[1].trim().substring(0, length) : "";
    }

    public String getMessage() {
        return message;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return String.valueOf(url);
    }

    public String getQueryParam(String name) {
        return queryParams.stream().filter(s -> s.getName().equals(name)).findFirst().get().getValue();
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
