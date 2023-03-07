import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.addHandler(HTTPMethod.GET, "/public", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                try(final BufferedOutputStream out = responseStream) {
                    final String path = request.getUrl();
                    Response response = new Response();
                    File f = new File("." + path);
                    if(f.exists() && !f.isDirectory()) {
                        response.setBodyInFile(path);
                    } else {
                        response.setStatusCode(404);
                        response.setStatus("Not found");
                        response.setBodyInFile("/public/404.html");
                    }
                    out.write(response.getByte());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        server.start();
    }
}