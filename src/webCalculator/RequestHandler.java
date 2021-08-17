package webCalculator;



import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;



import org.json.simple.parser.JSONParser;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;


public class RequestHandler {
    
    public static void send200Answer(HttpExchange exchange, String ResponseData) throws IOException
    {
        exchange.sendResponseHeaders(200, ResponseData.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(ResponseData.getBytes());
        output.flush();
        exchange.close();
    }

    public static void sendBadRequestError(HttpExchange exchange, String ResponseData) throws IOException{
        
        exchange.sendResponseHeaders(400, ResponseData.getBytes().length);
        OutputStream output = exchange.getResponseBody();
        output.write(ResponseData.getBytes());
        output.flush();
        exchange.close();
    }
    
    public static void requestHelloHandler(HttpServer server){
        server.createContext("/request", exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            String requestURI = exchange.getRequestURI().toString();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            if (requestURI.equals("/request")){
                String respText = "Java REST API works perfectllly!\n";
                send200Answer(exchange, respText);
            } else {
                String respText = requestURI.substring("/request".length()+1, requestURI.length());
                respText = respText.replace("/", "\n");
                send200Answer(exchange, respText);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    
        });
    }   
    public static void requestNewsHandler(HttpServer server){
        server.createContext("/news", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
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
                send200Answer(exchange, ResponseString);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        });
    }

    public static void requestJSONHandler(HttpServer server){
        server.createContext("/calc", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                JSONParser parser = new JSONParser();
                String data = IOUtils.toString(exchange.getRequestBody(), "UTF-8");
                try {
                    Boolean error = false;
                    JSONObject resultJson = new JSONObject();
                    JSONArray result_objects = new JSONArray();
                    JSONObject json_data = (JSONObject) parser.parse(data);
                    JSONArray objects = (JSONArray) json_data.get("objects");
                    for (Object item: objects){
                        JSONObject json_item = (JSONObject) item;
                        String operations = (String) json_item.get("operations");
                        if (operations.replaceAll("[+/*-]*", "").length() == 0){                   
                            int[] digits = convertJsonArray((JSONArray) json_item.get("numbers"));
                            int result = digits[0];
                            for (int i = 1; i < digits.length; i++) {
                                switch (operations.charAt(i-1)) {
                                    case ('+'):{
                                        result = calc_operations.addition(result, digits[i]);
                                        break;
                                    }
                                    case ('-'):{
                                        result = calc_operations.subtration(result, digits[i]);
                                        break;
                                    }
                                    case ('*'):{
                                        result = calc_operations.multiplication(result, digits[i]);
                                        break;
                                    }
                                    case ('/'):{
                                        if (digits[i] != 0){
                                            result = calc_operations.division(result, digits[i]);
                                            break;
                                        } else {
                                            error = true;
                                            String ResponseData = "Error. Zero Divison Exception";
                                            sendBadRequestError(exchange, ResponseData);
                                            break;
                                        }
                                    }
                                }
                            }
                            JSONObject tmp = new JSONObject();
                            tmp.put("result", result);
                            result_objects.add(tmp);
                        } else {
                            error = true;
                            String ResponseData = "Error. Check input data.";
                            sendBadRequestError(exchange, ResponseData);
                            break;
                        }
                    }
                    if (!error) {
                        resultJson.put("status", "success");
                        resultJson.put("objects", result_objects);
                        String ResponseData = resultJson.toString();
                        send200Answer(exchange, ResponseData);
                    }
                    
                } catch (Exception e) {
                    System.out.print(e);
                }
                
                
            }
        });

    }

    public static int[] convertJsonArray(JSONArray jsonArray){
        int[] list;
        if (jsonArray != null) { 
            int len = jsonArray.size();
            list = new int[len];
            for (int i = 0; i < len; i++){ 
                String tmp = (String) (jsonArray.get(i) + "");
                list[i] =  Integer.parseInt(tmp);
            }    
        }
        else {
            list = null;
        }
        return list;
    }
    public static String convertMap(Map<String, ?> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key + "=" + map.get(key) + ", ");
        }
        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}\n");
        return mapAsString.toString();
    }

}  
