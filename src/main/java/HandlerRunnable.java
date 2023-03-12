import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HandlerRunnable implements Runnable {

    Socket socket;

    public HandlerRunnable(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            StringBuilder builder = new StringBuilder();
            String line;
            while (in.ready()) {
                line = in.readLine();
                builder.append(line).append("\r\n");
            }
            if (builder.isEmpty()) {
                System.out.println("Пустой запрос: " + socket.getRemoteSocketAddress());
                return;
            }
            Request request = new Request(builder.toString());
            System.out.println("Клиент - " + socket.getRemoteSocketAddress());
            System.out.println(request.getMethod());
            System.out.println(request.getUrl());
            System.out.println(request.getHeaders());
            request.getPostParams().forEach(System.out::println);

            for (var letterEntry : Server.handlerMap.entrySet()) {
                Handler handler = letterEntry.getValue();
                for (var Entry : letterEntry.getKey().entrySet()) {
                    HTTPMethod httpMethod = Entry.getKey();
                    String path = Entry.getValue();
                    if (request.getUrl().startsWith(path)) {
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
        } finally {
            try {
                socket.close();
                System.out.printf("Клиент %s отключен!%n%n", socket.getRemoteSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}