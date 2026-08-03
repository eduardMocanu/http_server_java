import handlers.EchoHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Utils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;



public class EchoHandlerTest {

    private static EchoHandler echoHandler;

    @BeforeAll
    static void setup(){
        echoHandler = new EchoHandler();
    }

    @Test
    public void emptyMessageTest() throws IOException {
        var request = Utils.parseRawRequest("GET /echo/ HTTP/1.1\r\n\r\n\r\n");

        var response = echoHandler.handle(request);

        assertEquals(404, response.getResponseCode());
    }

    @Test
    public void validMessageTest() throws IOException {
        var request = Utils.parseRawRequest("GET /echo/123 HTTP/1.1\r\n\r\n\r\n");

        var response = echoHandler.handle(request);

        assertEquals(200, response.getResponseCode());
    }


}
