import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {
    final List<String> validPaths =
            List.of("/index.html", "/spring.svg", "/spring.png",
                    "/resources.html", "/styles.css", "/app.js",
                    "/links.html", "/forms.html", "/classic.html",
                    "/events.html", "/events.js");

    private int port;
    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
