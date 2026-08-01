package handlers;

import exceptions.InvalidHeader;
import http.HttpRequest;
import http.HttpResponse;

public class UserAgentHandler implements RequestHandler {
    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            return HttpResponse.ok(userAgent);
        }catch (InvalidHeader e){
            return HttpResponse.notFound("There is no user agent");
        }
    }
}
