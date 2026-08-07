import exceptions.EmptyRequest;
import exceptions.InvalidHeader;
import exceptions.MalformedRequest;
import http.HttpRequest;
import org.junit.jupiter.api.Test;
import utils.Utils;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {



    @Test
    void parseMethodAndPathTest() throws IOException{
        HttpRequest req = Utils.parseRawRequest("GET /go/123 HTTP/1.1\r\n\r\n");

        assertEquals("/go/123", req.getPath());
        assertEquals("GET", req.getMethod());
    }

    @Test
    void parseHeadersTest() throws IOException{
        HttpRequest req = Utils.parseRawRequest("GET /go/123 HTTP/1.1\r\n" +
                "User-Agent: curl/8.0\r\n" +
                "\r\n");

        assertEquals("curl/8.0", req.getHeader("User-Agent"));
        assertThrows(InvalidHeader.class, () -> req.getHeader("adadaw"));

    }

    @Test
    void getHeadersTest() throws IOException{
        HttpRequest req = Utils.parseRawRequest(
                "GET /go/123 HTTP/1.1\r\n" +
                "User-Agent: curl/8.0\r\n" +
                "\r\n");
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "curl/8.0");

        assertEquals(req.getHeaders(), headers);
    }

    @Test
    void malformedRequestLineTest(){
        assertThrows(MalformedRequest.class, () -> Utils.parseRawRequest("GET HTTP/1.1\r\n\r\n\r\n"));
    }

    @Test
    void emptyRequestLineTest(){
        assertThrows(EmptyRequest.class, () -> Utils.parseRawRequest(""));
    }

    @Test
    void parseBodyTest() throws IOException{
        String body = "Ana are mere";
        HttpRequest req = Utils.parseRawRequest(Utils.buildPostRequest("/files/test", body));

        assertArrayEquals(body.getBytes(), req.getBody());
    }

    @Test
    void parseBodyEmptyTest() throws IOException{
        HttpRequest req = Utils.parseRawRequest("POST /files/test HTTP/1.1\r\n\r\n");

        assertArrayEquals(new byte[0], req.getBody());
    }

    @Test
    void parseBodyLengthMismatchTest(){
        String rawRequest = "POST /files/test HTTP/1.1\r\n" +
                "Content-Length: 100\r\n" +
                "\r\n" +
                "Ana are mere";

        assertThrows(MalformedRequest.class, () -> Utils.parseRawRequest(rawRequest));
    }
}
