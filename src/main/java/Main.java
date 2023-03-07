import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.addHandler(HTTPMethod.GET, "/public", (request, responseStream) -> {
            try (final BufferedOutputStream out = responseStream) {
                final String path = request.getUrl();
                Response response = new Response();
                if (path.contains("classic.html")) {
                    response.setBodyOldSchool(path);
                } else {
                    response.setBodyInFile(path);
                }
                out.write(response.getByte());
                out.flush();
            }
        });


        server.start();
    }
}