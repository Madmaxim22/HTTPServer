import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.addHandler(HTTPMethod.GET, "/index.html", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                try(final BufferedOutputStream out = responseStream) {
                    final String path = request.getUrl();
                    Response response = new Response();
                    response.setBodyInFile(path);
                    out.write(response.getByte());
                    out.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        server.start();
    }
}