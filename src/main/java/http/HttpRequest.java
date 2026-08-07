package http;

import exceptions.EmptyRequest;
import exceptions.InvalidHeader;
import exceptions.MalformedRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final Map<String, String> headers;
    private final String method;
    private final String path;
    private final byte[] body;

    private HttpRequest(Map<String, String> headers, String method, String path, byte[] body) {
        this.headers = headers;
        this.method = method;
        this.path = path;
        this.body = body;
    }

    static public HttpRequest parse(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        System.out.println(requestLine);
        if (requestLine != null && !requestLine.isEmpty()) {
            String[] parts = requestLine.split(" ");
            if (parts.length < 3) {
                throw new MalformedRequest("The request line is malformed");
            }
            String method = parts[0];
            String path = parts[1];
            Map<String, String> headers = parseHeaders(reader);

            byte[] body = parseBody(reader, Integer.parseInt(headers.getOrDefault("Content-Length", "0")));

            return new HttpRequest(headers, method, path, body);
        } else {
            throw new EmptyRequest("The request line is empty");
        }
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String requestLine;
        while (((requestLine = reader.readLine()) != null) && !requestLine.isEmpty()) {
            if (requestLine.startsWith("User-Agent:")) {
                headers.put("User-Agent", requestLine.substring("User-Agent:".length()).trim());
            }
            if (requestLine.startsWith("Host:")) {
                headers.put("Host", requestLine.substring("Host:".length()).trim());
            }
            if (requestLine.startsWith("Accept:")) {
                headers.put("Accept", requestLine.substring("Accept:".length()).trim());
            }
            if (requestLine.startsWith("Content-Type:")) {
                headers.put("Content-Type", requestLine.substring("Content-Type:".length()).trim());
            }
            if (requestLine.startsWith("Content-Length:")) {
                headers.put("Content-Length", requestLine.substring("Content-Length:".length()).trim());
            }
            if (requestLine.startsWith("Content-Encoding:")) {
                headers.put("Content-Encoding", requestLine.substring("Content-Encoding:".length()).trim());
            }
            if (requestLine.startsWith("Accept-Encoding:")) {
                headers.put("Accept-Encoding", requestLine.substring("Accept-Encoding:".length()).trim());
            }
            if (requestLine.startsWith("Connection:")) {
                headers.put("Connection", requestLine.substring("Connection:".length()).trim());
            }
        }
        return headers;
    }

    private static byte[] parseBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0){
            return new byte[0];
        }
        char[] buff = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength){
            int n = reader.read(buff, totalRead, contentLength - totalRead);
            if ( n == -1){
                break;
            }
            totalRead += n;
        }

        if (totalRead < contentLength){
            throw new MalformedRequest("The content length doesn't match the body size");
        }

        byte[] body = new byte[totalRead];
        for (int i =0; i<totalRead; i++){
            body[i] = (byte) buff[i];
        }
        return body;
    }

    public String getHeader(String header) {
        if (headers.containsKey(header)) {
            return headers.get(header);
        } else {
            throw new InvalidHeader("The wanted header is not in the request headers");
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}