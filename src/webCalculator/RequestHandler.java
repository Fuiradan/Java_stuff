package webCalculator;


import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;


import com.sun.net.httpserver.HttpServer;


public class RequestHandler {
    
    public static void requestHelloHandler(HttpServer server){
        server.createContext("/request", exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().toString();
            if (requestURI.equals("/request")){
                String respText = "Java REST API works perfectllly!\n";
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
                exchange.close();
            } else {
                String respText = requestURI.substring("/request".length()+1, requestURI.length());
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
    public static void requestNewsHandler(HttpServer server){
        server.createContext("/news", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                String[] params = new String[]{"msid","mlid","utm_source","utm_medium"};
                String regexTemplate = "(?<data>.+?(?=&|$))";
                String requestURI = exchange.getRequestURI().toString();
                Pattern pattern;
                Matcher matcher;
                Map<String, String> ResponseData = new HashMap<String, String>();
                for (String parameter : params){
                    pattern = Pattern.compile("(?:" + parameter +"=)"+ regexTemplate);
                    matcher = pattern.matcher(requestURI);
                    if (matcher.find()) {
                        String found_data = matcher.group("data");
                        ResponseData.put(parameter, found_data);
                    }
                }
                String ResponseString = convertMap(ResponseData);
                exchange.sendResponseHeaders(200, ResponseString.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(ResponseString.getBytes());
                output.flush();
                exchange.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
    }

    public static String convertMap(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }

}  
