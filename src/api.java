import java.io.IOException;
import java.net.InetSocketAddress;
import webCalculator.RequestHandler;

import com.sun.net.httpserver.HttpServer;

public class api {

    public static void main(String[] args) throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        RequestHandler.requestHelloHandler(server);
        RequestHandler.requestNewsHandler(server);
        RequestHandler.requestJSONHandler(server);
        server.setExecutor(null);
        server.start();
    }
}

