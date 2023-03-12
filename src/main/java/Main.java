import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(6374);

        server.addHandler(HTTPMethod.GET, "/public", (request, responseStream) -> {
            try (final BufferedOutputStream out = responseStream) {
                final String path = request.getUrl();
                Response response = new Response();
                File f = new File("." + path);
                if (f.exists() && !f.isDirectory()) {
                    if (path.contains("classic.html")) {
                        response.setBodyOldSchool(path);
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

        server.addHandler(HTTPMethod.GET, "/static", (request, responseStream) -> {
            try (final BufferedOutputStream out = responseStream) {
                final String path = request.getUrl();
                Response response = new Response();
                File f = new File("." + path);
                if (f.exists() && !f.isDirectory()) {
                    response.setBodyInFile(path);
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