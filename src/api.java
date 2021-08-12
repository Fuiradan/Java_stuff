import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class api {

    public static void main(String[] args) throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/request", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                String respText = "Java REST API works!\n";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }));
        /*server.createContext("/req_with_param", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
            }
        }));*/
        server.setExecutor(null);
        server.start();
    }
}

