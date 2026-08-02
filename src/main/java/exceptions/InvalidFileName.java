package exceptions;

public class InvalidFileName extends RuntimeException {
    public InvalidFileName(String message) {
        super(message);
    }
}
