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

    private HttpRequest(Map<String, String> headers, String method, String path) {
        this.headers = headers;
        this.method = method;
        this.path = path;
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
            return new HttpRequest(headers, method, path);
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
            if (requestLine.startsWith("Accept-Encoding:")) {
                headers.put("Accept-Encoding", requestLine.substring("Accept-Encoding:".length()).trim());
            }
            if (requestLine.startsWith("Connection:")) {
                headers.put("Connection", requestLine.substring("Connection:".length()).trim());
            }
        }
        return headers;
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
}