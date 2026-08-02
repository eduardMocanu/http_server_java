package http;

import exceptions.InvalidResponseCode;
import exceptions.RequestCanNotBeFulfilled;
import httpCodes.MessageCodes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final byte[] body;
    private final int responseCode;
    private final String responseMessage;
    private final Map<String, String> headers;


    public HttpResponse(int responseCode, byte[] body, Map<String, String> headers){
        this.responseCode = responseCode;
        this.body = body;
        this.headers = headers;
        try{
            this.responseMessage = MessageCodes.messageForResponseCode(responseCode);
        }catch (InvalidResponseCode e){
            throw new RequestCanNotBeFulfilled(e.getMessage());
        }
    }

    public byte[] getResponseInBytes(){
        String headerText = "HTTP/1.1 " + Integer.toString(this.responseCode) + " " + this.responseMessage + "\r\n"+
                headersToString() + "\r\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            out.write(headerText.getBytes());
            out.write(body);
        }catch (IOException e){
            //nothing
        }
        return out.toByteArray();
    }

    public String headersToString(){
        StringBuilder headersText = new StringBuilder();
        headersText.append("Content-Length: ").append(body.length).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return headersText.toString();
    }

    public static HttpResponse notFound(String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(404, body.getBytes(), headers);
    }

    public static HttpResponse ok(String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(200, body.getBytes(), headers);
    }

    public static HttpResponse ok(byte[] body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/octet-stream");
        return new HttpResponse(200, body, headers);
    }

    public static HttpResponse ok(String body, Map<String, String> extraHeaders) {
        Map<String, String> headers = new HashMap<>(extraHeaders);
        headers.putIfAbsent("Content-Type", "text/plain");
        return new HttpResponse(200, body.getBytes(), headers);
    }

    public static HttpResponse unauthorized(String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(401, body.getBytes(), headers);
    }

    public static HttpResponse internalServerError(String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(500, body.getBytes(), headers);
    }

    public static HttpResponse lengthRequired(String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(411, body.getBytes(), headers);
    }

    public static HttpResponse created(String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(201, body.getBytes(), headers);
    }

    public static HttpResponse badRequest(String body){
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new HttpResponse(400, body.getBytes(), headers);
    }

}
