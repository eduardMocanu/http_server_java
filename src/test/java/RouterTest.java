import handlers.*;
import http.HttpRequest;
import http.HttpResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import router.Router;
import utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;


public class RouterTest {


    @Test
    void echoRouteTest() throws IOException {
        var instance = Router.route(Utils.parseRawRequest("GET /echo/aaa HTTP/1.1\r\n\r\n\r\n"));

        assertInstanceOf(EchoHandler.class, instance);
    }

    @Test
    void filesGetRouteTest() throws IOException{
        var instance = Router.route(Utils.parseRawRequest("GET /files/dawd HTTP/1.1\r\n\r\n\r\n"));

        assertInstanceOf(FilesGetHandler.class, instance);
    }

    @Test
    void filesPostRouteTest() throws IOException{
        var instance = Router.route(Utils.parseRawRequest("POST /files/gafw HTTP/1.1\r\n\r\n\r\n"));

        assertInstanceOf(FilesPostHandler.class, instance);
    }

    @Test
    void userAgentRouteTest() throws IOException{
        var instance = Router.route(Utils.parseRawRequest("GET /user-agent HTTP/1.1\r\n\r\n\r\n"));

        assertInstanceOf(UserAgentHandler.class, instance);
    }

    @Test
    void notFoundRouteTest() throws IOException{
        var instance = Router.route(Utils.parseRawRequest("GET /fafw HTTP/1.1\r\n\r\n\r\n"));

        assertInstanceOf(NotFoundHandler.class, instance);
    }
}
