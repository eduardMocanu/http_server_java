package handlers;

import http.HttpRequest;
import http.HttpResponse;

public interface RequestHandler {
    public HttpResponse handle(HttpRequest request);
}
