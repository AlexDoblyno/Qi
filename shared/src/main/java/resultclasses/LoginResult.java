package resultclasses;

import com.google.gson.*;
import model.AuthToken;

import java.lang.reflect.Type;

public class LoginResult extends Result {

    private AuthToken authToken;

    public LoginResult() {
        super();
        this.authToken = null;
    }

    public LoginResult(AuthToken authToken) {
        super(200);
        this.authToken = authToken;
    }

    public LoginResult(int status, String message) {
        super(status, message);
        this.authToken = null;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public static class LoginResultTypeAdapter implements JsonSerializer<LoginResult>, JsonDeserializer<LoginResult> {

        @Override
        public JsonElement serialize(LoginResult loginResult, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
            if (loginResult.getAuthToken() != null) {
                jsonObject.addProperty("authToken", loginResult.getAuthToken().getAuthToken());
                jsonObject.addProperty("username", loginResult.getAuthToken().getUsername());
            }
            if (loginResult.getMessage() != null) {
                jsonObject.addProperty("message", loginResult.getMessage());
            }
            jsonObject.addProperty("status", loginResult.getStatus());
            return jsonObject;
        }

        @Override
        public LoginResult deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String authToken = null;
            if (jsonObject.has("authToken")) {
                authToken = jsonObject.get("authToken").getAsString();
            }

            String username = null;
            if (jsonObject.has("username")) {
                username = jsonObject.get("username").getAsString();
            }

            int status = 0;
            if (jsonObject.has("status")) {
                status = jsonObject.get("status").getAsInt();
            }

            String message = null;
            if (jsonObject.has("message")) {
                message = jsonObject.get("message").getAsString();
            }

            if (authToken != null && username != null) {
                return new LoginResult(new AuthToken(authToken, username));
            } else {
                return new LoginResult(status, message);
            }
        }
    }
}
