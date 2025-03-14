package server.responses;

public class LoginResponse {
    protected String authToken;
    protected String username;

    public LoginResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }


    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
