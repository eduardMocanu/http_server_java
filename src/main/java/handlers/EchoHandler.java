package handlers;


import http.HttpResponse;

import http.HttpRequest;

public class EchoHandler implements RequestHandler {

    @Override
    public HttpResponse handle(HttpRequest req) {
        String message = req.getPath().trim().substring(6);
        if (!message.isEmpty()) {
            return HttpResponse.ok(message);
        }
        return HttpResponse.notFound("The path is invalid");
    }

}
