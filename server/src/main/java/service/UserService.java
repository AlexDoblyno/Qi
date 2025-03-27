package service;

import dataaccess.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import static dataaccess.AuthDAO.*;
import static dataaccess.UserDAO.createUser;
import static dataaccess.UserDAO.getUser;

public class UserService implements Service {

    public RegisterResult register(RegisterRequest registerRequest)
            throws UnavailableException, BadRequestException, DataAccessException {
        if (registerRequest.username() == null ||
                registerRequest.email() == null ||
                registerRequest.password() == null) {
            throw new BadRequestException("Bad Request");
        }

        if (getUser(registerRequest.username()) == null) {
            String hashedPassword = BCrypt.hashpw(registerRequest.password(),
                    BCrypt.gensalt());
            UserData user = new UserData(registerRequest.username(),
                    hashedPassword,
                    registerRequest.email());
            createUser(user);

            String authToken = generateToken();
            AuthData auth = new AuthData(authToken, registerRequest.username());
            createAuth(auth);

            return new RegisterResult(registerRequest.username(),
                    authToken);
        } else {
            throw new UnavailableException("Already Taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {
        if (loginRequest.username() == null ||
                loginRequest.password() == null) {
            throw new BadRequestException("Bad Request");
        }

        UserData user = getUser(loginRequest.username());
        if (user != null) {
            if (BCrypt.checkpw(loginRequest.password(), user.password())) {
                String authToken = generateToken();
                AuthData auth = new AuthData(authToken, loginRequest.username());
                createAuth(auth);

                return new LoginResult(loginRequest.username(), authToken);
            } else {
                throw new UnauthorizedException("Unauthorized");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {

        if (logoutRequest == null || logoutRequest.authToken() == null) {
            throw new BadRequestException("Bad Request");
        }

        AuthData auth = getAuth(logoutRequest.authToken());
        if (auth != null) {
            deleteAuth(logoutRequest.authToken());
            return new LogoutResult();
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
