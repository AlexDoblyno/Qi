package requestclasses;

public class LogoutRequest extends Request {

    private String username;

    public LogoutRequest() {
            super();
        }

    public LogoutRequest(String authToken) {
            super(authToken);
        }

    public String getUsername() {
        return username;
    }
}
