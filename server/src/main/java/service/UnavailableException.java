package service;

/**
 * Indicates desired valued to add to database is already taken
 */
public class UnavailableException extends Exception {
    public UnavailableException(String message) {
        super(message);
    }
}