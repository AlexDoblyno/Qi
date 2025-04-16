package resultclasses;

public class Result {

    private String message;
    private int status;

    public Result() {
        this.message = null;
        this.status = 0;
    }

    public Result(int status) {
        this.message = null;
        this.status = status;
    }

    public Result(int status, String message) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

}