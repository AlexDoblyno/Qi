package resultclasses;

public class ClearDBResult extends Result {
    public ClearDBResult() {
        super();
    }

    public ClearDBResult(int status, String message) {
        super(status, message);
    }

    public ClearDBResult(int status) {
        super(status);
    }
}
