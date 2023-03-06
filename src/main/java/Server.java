import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final List<String> validPaths =
            List.of("/index.html", "/spring.svg", "/spring.png",
                    "/resources.html", "/styles.css", "/app.js",
                    "/links.html", "/forms.html", "/classic.html",
                    "/events.html", "/events.js");

    private int port;
    private final ServerSocket serverSocket;
    private final ExecutorService service;
    Map<Map<HTTPMethod, String>, Handler> handlerMap;

    public Server(int port) throws IOException {
        this.port = port;
        this.handlerMap = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newFixedThreadPool(64);
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                service.submit(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        StringBuilder builder = new StringBuilder();
                        while (in.ready()) {
                            builder.append(in.readLine());
                            builder.append("\r\n");
                        }
                        Request request = new Request(builder.toString());
                        for (var letterEntry : handlerMap.entrySet()) {
                            Handler handler = letterEntry.getValue();
                            for (var Entry : letterEntry.getKey().entrySet()) {
                                HTTPMethod httpMethod = Entry.getKey();
                                String message = Entry.getValue();
                                if (httpMethod.equals(HTTPMethod.GET)){
                                    if (message.equals(request.getUrl())) {
                                        Map<HTTPMethod, String> map = new HashMap<>();
                                        map.put(httpMethod, message);
                                        handler = handlerMap.get(map);
                                        handler.handle(request, new BufferedOutputStream(clientSocket.getOutputStream()));
                                    }
                                }
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handler(Socket clientSocket) {
        try (
                final var socket = serverSocket.accept();
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }

            final var path = parts[1];
            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                return;
            }

            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
