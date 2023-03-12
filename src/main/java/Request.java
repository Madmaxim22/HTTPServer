import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {

    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEW_LINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";
    private final String message;
    private final HTTPMethod method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;
    private final List<NameValuePair> postParams = new ArrayList<>();

    public Request(String message) {
        this.message = message;
        String[] parts = message.split(DELIMITER);
        String head = parts[0];
        String[] headers = head.split(NEW_LINE);
        String[] firstLine = headers[0].split(" ");
        method = HTTPMethod.valueOf(firstLine[0]);
        url = firstLine[1];
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
        this.body = length > 1 ? URLDecoder.decode(parts[1].trim().substring(0, length), StandardCharsets.UTF_8) : "";
        if (!this.body.isEmpty()) {
            try {
                String[] bodyParts = body.split("&");
                for (String part : bodyParts) {
                    String[] param = part.split("=", 2);
                    NameValuePair nameValuePair = new NameValuePair(param[0], param[1]);
                    postParams.add(nameValuePair);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public List<NameValuePair> getPostParams() {
        return postParams;
    }

    public String getPostParam(String name) {
        return postParams.stream().filter(s -> s.getName().equals(name)).findFirst().get().getValue();
    }
}
