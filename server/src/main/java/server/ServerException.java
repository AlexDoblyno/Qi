package server;

public class ServerException extends Exception {
    String message;
    int statusCode;

    public ServerException(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return "{ \"message\": \"" + message + "\" }";
    }

    public int getStatusCode() {
        return statusCode;
    }
}
