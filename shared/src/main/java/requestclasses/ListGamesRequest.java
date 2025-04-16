package requestclasses;

public class ListGamesRequest extends Request {

    public ListGamesRequest() {
        super();
    }

    public ListGamesRequest(String authToken) {
        super(authToken);
    }
}
