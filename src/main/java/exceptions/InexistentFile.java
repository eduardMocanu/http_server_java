package exceptions;

public class InexistentFile extends RuntimeException {
    public InexistentFile(String message) {
        super(message);
    }
}
