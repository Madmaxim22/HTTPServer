import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ServerSocket serverSocket;
    private final ExecutorService service;
    Map<Map<HTTPMethod, String>, Handler> handlerMap;

    public Server(int port) throws IOException {
        this.handlerMap = new ConcurrentHashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.service = Executors.newFixedThreadPool(4);
    }

    public void start() {
        System.out.println("Сервер запущен!");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое соединение: " + clientSocket.getRemoteSocketAddress());
                service.submit(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
                        StringBuilder builder = new StringBuilder();
                        while (in.ready()) {
                            builder.append(in.readLine());
                            builder.append("\r\n");
                        }
                        if (builder.isEmpty()) return;
                        Request request = new Request(builder.toString());
                        for (var letterEntry : handlerMap.entrySet()) {
                            Handler handler = letterEntry.getValue();
                            for (var Entry : letterEntry.getKey().entrySet()) {
                                HTTPMethod httpMethod = Entry.getKey();
                                String message = Entry.getValue();
                                if (request.getPath().contains(message)) {
                                    if (httpMethod.equals(HTTPMethod.GET)) {
                                        handler.handle(request, out);
                                    } else if (httpMethod.equals(HTTPMethod.POST)) {
                                        handler.handle(request, out);
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
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
