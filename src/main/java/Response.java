import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// TODO implement builder pattern
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

    public byte[] getByte() {
        return message().getBytes();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.headers.put("Content-Length", String.valueOf(body.length()));
        this.body = body;
    }

    public void setBodyInFile(String path) {
        final Path filePath = Path.of(".", path);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new FileReader(filePath.toFile()))) {
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
            final String mimeType = Files.probeContentType(filePath);
            addHeader("Content-Type", mimeType);
            String content = builder.toString();
            setBody(content);
        } catch (IOException e) {
            e.printStackTrace();
            this.setStatusCode(404);
            this.setStatus("Not found");
            this.setBody("<html><head><body><h1>Resource not found</h1></body></html>");
        }
    }

    public void setBodyOldSchool(String path) {
        try {
            final Path filePath = Path.of(".", path);
            final String template = Files.readString(filePath);
            final String content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString());
            final String mimeType = Files.probeContentType(filePath);
            addHeader("Content-Type", mimeType);
            setBody(content);
        } catch (IOException e) {
            e.printStackTrace();
            this.setStatusCode(404);
            this.setStatus("Not found");
            this.setBody("<html><head><body><h1>Resource not found</h1></body></html>");
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
