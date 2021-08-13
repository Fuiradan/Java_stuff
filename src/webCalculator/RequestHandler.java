package webCalculator;


import java.io.OutputStream;
import com.sun.net.httpserver.HttpServer;


public class RequestHandler {
    
    public static void requestHelloHandler(HttpServer server){
        server.createContext("/request", exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            String reqURI = exchange.getRequestURI().toString();
            if (reqURI.equals("/request")){
                String respText = "Java REST API works perfectllly!\n";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            } else {
                String respText = reqURI.substring("/request".length()+1, reqURI.length());
                respText = respText.replace("/", "\n");
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    
    });
}
}  
