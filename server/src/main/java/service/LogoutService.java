package service;

import dataaccess.AuthSQL;
import dataaccess.DataAccessException;
import requestclasses.LogoutRequest;
import resultclasses.LogoutResult;
import server.ServerException;

public class LogoutService extends Service {

    public LogoutService() {
        super();
    }

    public LogoutService(AuthSQL authSQL, UserSQL userSQL) {
        super(authSQL, null, userSQL);
    }

    public LogoutResult logout(LogoutRequest request) throws ServerException {
        //Check if authentication token or username is null
        if (request.getAuthToken() == null) {
            throw new ServerException("Error: bad request", 400);
        }

        //Check if authentication token is valid
        try {
            var token = authSQL.checkAuthToken(request.getAuthToken());
        } catch (DataAccessException e) {
            throw new ServerException("Error: unauthorized", 401);
        } catch (Exception e) {
            throw new ServerException("Error: " + e.getMessage(), 500);
        }

        //Delete auth token
        try {
            authSQL.deleteAuthToken(request.getAuthToken());
        } catch (Exception e) {
            throw new ServerException("Error: " + e.getMessage(), 500);
        }

        return new LogoutResult(200);
    }
}