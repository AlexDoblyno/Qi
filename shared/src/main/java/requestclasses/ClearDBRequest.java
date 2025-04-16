package requestclasses;

public class ClearDBRequest extends Request {
    public ClearDBRequest() {
    }

    public ClearDBRequest(String authToken) {
        super(authToken);
    }
}
