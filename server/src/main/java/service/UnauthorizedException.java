package service;

/**
 * Indicates the authToken didn't check out
 */
public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }
}
