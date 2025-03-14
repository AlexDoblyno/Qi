package service;

/**
 * Indicates request was invalid
 */
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}