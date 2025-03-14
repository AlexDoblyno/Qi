package server.handlers;

import com.google.gson.Gson;
import spark.Response;
import java.util.Map;

public class ErrorHandler {
    private final Gson serializer = new Gson();

    public Object handleError(Exception e, Response res, int statusCode) {
        String body = serializer.toJson(Map.of("message", "Error: " + e.getMessage(), "success", false));
        res.type("application/json");
        res.status(statusCode);
        return body;
    }
}