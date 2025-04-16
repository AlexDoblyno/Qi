package requestclasses;

public class LoginRequest extends Request {

    private String username;
    private String password;

    public LoginRequest() {
        super();
        this.username = null;
        this.password = null;
    }

    public LoginRequest(String username, String password) {
        super();
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
