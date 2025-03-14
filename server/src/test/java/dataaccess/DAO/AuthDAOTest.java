package dataaccess.DAO;

import dataaccess.exceptions.BadRequestException;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private AuthDAO AuthDAO;

    @BeforeEach
    void setUp() throws DataAccessException, BadRequestException {
        (AuthDAO = new AuthDAO()).clear();
    }

    @Test
    void createValidAuth() {
        assertDoesNotThrow(() -> {
            AuthData newAuth = AuthDAO.createAuth("testUser");});
    }

    @Test
    void createInvalidAuth() {
        assertThrows(DataAccessException.class, () -> {AuthDAO.createAuth(null);});
    }

    @Test
    void getAuthByToken() throws DataAccessException {
        AuthData newAuth = AuthDAO.createAuth("testUser");
        assertDoesNotThrow(() -> {AuthDAO.getAuthByToken(newAuth.authToken());});
    }

    @Test
    void getAuthByBadToken() throws DataAccessException {
        assertNull(AuthDAO.getAuthByToken("badToken"));
    }

    @Test
    void deleteValidAuth() throws DataAccessException {
        AuthData newAuth = AuthDAO.createAuth("testUser");
        assertDoesNotThrow(() -> {AuthDAO.deleteAuth(newAuth.authToken());});
        assertNull(AuthDAO.getAuthByToken(newAuth.authToken()));
    }

    @Test
    void clear() {
        assertDoesNotThrow(() -> {AuthDAO.clear();});
    }
}