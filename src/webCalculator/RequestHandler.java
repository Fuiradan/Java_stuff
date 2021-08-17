package webCalculator;

import webCalculator.calc_operations;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;



import org.json.simple.parser.JSONParser;

import org.apache.commons.io.IOUtils;


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

    public static void requestJSONHandler(HttpServer server){
        server.createContext("/calc", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())){
                JSONParser parser = new JSONParser();
                String data = IOUtils.toString(exchange.getRequestBody(), "UTF-8");
                try {
                    Map<String, String> ResponseData = new HashMap<String, String>();
                    JSONObject json_data = (JSONObject) parser.parse(data);
                    JSONArray objects = (JSONArray) json_data.get("objects");
                    for (Object item: objects){
                        JSONObject json_item = (JSONObject) item;
                        String operations = (String) json_item.get("operations");
                        int[] digits = convertJsonArray((JSONArray) json_item.get("numbers"));
                        int result = digits[0];
                        System.out.print(digits);
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
                                    result = calc_operations.division(result, digits[i]);
                                    break;
                                }
                            }
                        }
                           
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
