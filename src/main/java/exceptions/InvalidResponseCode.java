package exceptions;

public class InvalidResponseCode extends RuntimeException {
    public InvalidResponseCode(String message) {
        super(message);
    }
}
