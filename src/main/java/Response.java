import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final static String NEW_LINE = "\r\n";
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";
    private int statusCode = 200;
    private String status = "Ok";

    public Response() {
        headers.put("Server", "naive");
        headers.put("Connection", "Close");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public String message() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(status)
                .append(NEW_LINE);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NEW_LINE);
        }
        return builder
                .append(NEW_LINE)
                .append(body)
                .toString();
    }
}