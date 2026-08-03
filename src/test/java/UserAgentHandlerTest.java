import handlers.UserAgentHandler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.Utils;


import java.io.IOException;


import static org.junit.jupiter.api.Assertions.*;


public class UserAgentHandlerTest {

    static private UserAgentHandler userAgentHandler;

    @BeforeAll
    public static void setup(){
        userAgentHandler = new UserAgentHandler();
    }

    @Test
    public void validAgentTest() throws IOException {
        var request = Utils.parseRawRequest("GET /user-agent/ HTTP/1.1\r\n" +
                "User-Agent: Test\r\n" +
                "\r\n");

        var response = userAgentHandler.handle(request);

        assertEquals(200, response.getResponseCode());
    }

    @Test
    public void invalidAgentTest() throws IOException {
        var request = Utils.parseRawRequest("GET /user-agent/ HTTP/1.1\r\n" +
                "\r\n\r\n");

        var response = userAgentHandler.handle(request);

        assertEquals(404, response.getResponseCode());
    }

}
