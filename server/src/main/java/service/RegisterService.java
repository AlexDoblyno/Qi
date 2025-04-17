package service;

import dataaccess.AuthSQL;
import dataaccess.DataAccessException;
import model.AuthToken;
import model.User;
import requestclasses.RegisterRequest;
import resultclasses.RegisterResult;

public class RegisterService extends Service {

    public RegisterService() {
        super();
    }

    public RegisterService(AuthSQL authSQL, UserSQL userSQL) {
        super(authSQL, null, userSQL);
    }

    public RegisterResult register(RegisterRequest request) {
        //Check if username or password or email are null
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            return new RegisterResult(400, "Error: bad request");
        }

        //Check if username is already taken
        try {
            var user = new User(request.getUsername(), request.getPassword(), request.getEmail());
            userSQL.addUser(user);
        } catch (DataAccessException e) {
            return new RegisterResult(403, "Error: already taken");
        } catch (Exception e) {
            return new RegisterResult(500, "Error: " + e.getMessage());
        }

        //Add auth token
        var token = new AuthToken(request.getUsername());
        try {
            authSQL.addAuthToken(token);
        } catch (Exception e) {
            return new RegisterResult(500, "Error: " + e.getMessage());
        }
        return new RegisterResult(token, request.getUsername());
    }
}
