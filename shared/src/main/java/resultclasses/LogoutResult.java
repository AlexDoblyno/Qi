package resultclasses;

import model.AuthToken;

public class LogoutResult extends Result {

    private AuthToken authToken;

    public LogoutResult() {
        super();
    }

    public LogoutResult(int status) {
        super(status);
    }

    public LogoutResult(int status, String message) {
        super(status, message);
    }

    public AuthToken getAuthToken() {
        return authToken;
    }
}
