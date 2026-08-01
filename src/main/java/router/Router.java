package router;

import handlers.*;
import http.HttpRequest;

public abstract class Router {

    static public RequestHandler route(HttpRequest req) {
        if (req.getPath().startsWith("/echo/")) {
            return new EchoHandler();
        } else if (req.getPath().startsWith("/files/")) {
            if (req.getMethod().equals("GET")) {
                return new FilesGetHandler();
            } else if (req.getMethod().equals("POST")) {
                return new FilesPostHandler();
            }
        } else if (req.getPath().equals("/user-agent")) {
            return new UserAgentHandler();
        }
        return new NotFoundHandler();
    }

}
