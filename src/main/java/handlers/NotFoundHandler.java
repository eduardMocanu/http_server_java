package handlers;

import http.HttpRequest;
import http.HttpResponse;

public class NotFoundHandler implements RequestHandler{

    @Override
    public HttpResponse handle(HttpRequest request) {
        return HttpResponse.notFound("The wanted endpoint is not operational!");
    }
}
