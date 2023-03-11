import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.addHandler(HTTPMethod.GET, "/public", (request, responseStream) -> {
            try (final BufferedOutputStream out = responseStream) {
                final String path = request.getPath();
                Response response = new Response();
                File file = new File("." + path);
                if (file.exists() && !file.isDirectory()) {
                    if (path.contains("classic.html")) {
                        response.setBodyOldSchool(path);
                    } else if (request.getUrl().contains("?")) {
                        response.addHeader("Content-Type", "text/html; charset=utf-8    ");
                        response.setBody(
                                String.format("<html><body><h1>%s - Успешно добавлен</h1></body></html>"
                                        , request.getQueryParam("login")));
                    } else {
                        response.setBodyInFile(path);
                    }
                } else {
                    response.setStatusCode(404);
                    response.setStatus("Not found");
                    response.setBodyInFile("/error/404.html");
                }
                out.write(response.getByte());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.start();
    }
}