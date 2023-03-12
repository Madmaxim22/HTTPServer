import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static Map<Map<HTTPMethod, String>, Handler> handlerMap;
    private final ServerSocket serverSocket;
    private final ExecutorService service;

    public Server(int port) throws IOException {
        handlerMap = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newFixedThreadPool(64);
    }

    public void start() {
        System.out.println("Сервер запущен!");
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Новое подключение: " + socket.getRemoteSocketAddress());
                service.submit(new HandlerRunnable(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addHandler(HTTPMethod method, String messages, Handler handler) {
        Map<HTTPMethod, String> map = new HashMap<>();
        map.put(method, messages);
        handlerMap.put(map, handler);
    }
}
