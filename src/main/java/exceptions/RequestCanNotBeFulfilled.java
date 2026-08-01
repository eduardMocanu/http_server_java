package exceptions;

public class RequestCanNotBeFulfilled extends RuntimeException {
    public RequestCanNotBeFulfilled(String message) {
        super(message);
    }
}
