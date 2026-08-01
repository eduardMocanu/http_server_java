import exceptions.RequestCanNotBeFulfilled;
import http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class HttpResponseTest {
    private static HttpResponse response;
    @BeforeAll
    static void constructResponse(){
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "Test");
        response = new HttpResponse(200, "ana are mere".getBytes(), headers);
    }

    @Test
    void headersToStringTest(){
        String headersString = response.headersToString();
        String headerTest = "Content-Length: 12\r\nHost: Test\r\n";

        assertEquals(headerTest, headersString);
    }

    @Test
    void invalidResponseCodeTest(){
            byte[] arr = new byte[2];
            assertThrows(RequestCanNotBeFulfilled.class, () -> new HttpResponse(101, arr, new HashMap<>()));
    }

    @Test
    void getResponseInBytesTest(){
        String responseText = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 12\r\n" +
                "Host: Test\r\n" +
                "\r\n";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(responseText.getBytes());
            out.write("ana are mere".getBytes());
            assertArrayEquals(out.toByteArray(), response.getResponseInBytes());
        }catch (IOException e){
            //nothing
        }
    }
}
