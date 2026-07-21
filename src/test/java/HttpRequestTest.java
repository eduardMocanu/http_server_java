import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {

    private HttpRequest parse(String rawRequest) throws IOException {
        return HttpRequest.parse(new BufferedReader(new StringReader(rawRequest)));
    }

    @Test
    void parseMethodAndPath() throws IOException{
        HttpRequest req = parse("GET /go/123 HTTP/1.1\r\n\r\n");

        assertEquals(req.getPath(), req.getPath());
        assertEquals(req.getMethod(), req.getMethod());
    }

    @Test
    void parseHeaders() throws IOException{
        HttpRequest req = parse("GET /go/123 HTTP/1.1\r\n" +
                "User-Agent: curl/8.0\r\n" +
                "\r\n");
        assertEquals(req.getHeader("User-Agent"), "curl/8.0");
    }

    @Test
    void malformedRequestLine(){
        assertThrows(MalformedRequest.class, () -> parse("GET HTTP/1.1\r\n\r\n\r\n"));
    }

    @Test
    void emptyRequestLine(){
        assertThrows(EmptyRequest.class, () -> parse(""));
    }
}
