package server.requests;

public class LoginRequest {
    protected String username;
    protected String password;

    public LoginRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
