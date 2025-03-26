package dataaccess;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlAuthDAOTests {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        DatabaseManager.createTables();
        connection = DatabaseManager.getConnection();
        authDAO = new SqlAuthDAO();
        userDAO = new SqlUserDAO();
        authDAO.clear();  // Start with a clean table
        userDAO.clear();

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
            stmt.setString(1, "testUser");
            stmt.setString(2, "password");
            stmt.setString(3, "testEmail");
            stmt.executeUpdate();
            stmt.setString(1, "testUser2");
            stmt.setString(2, "password2");
            stmt.setString(3, "testEmail2");
            stmt.executeUpdate();
        }
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        authDAO.clear();  // Clean up after tests
        userDAO.clear();
        connection.close();
    }

    @Test
    void testClearSuccess() throws DataAccessException {
        authDAO.createAuthToken("testUser");
        assertEquals(1, authDAO.getSize());
        authDAO.clear();
        assertEquals(0, authDAO.getSize());
    }

    @Test
    void testGetUsernameSuccess() throws DataAccessException {
        String authToken = authDAO.createAuthToken("testUser");
        assertEquals("testUser", authDAO.getUsername(authToken));
    }

    @Test
    void testGetUsernameFailure() throws DataAccessException {
        assertNull(authDAO.getUsername("nonexistent_token"));
    }

    @Test
    void testCreateAuthTokenSuccess() throws DataAccessException {
        String authToken = authDAO.createAuthToken("testUser");
        assertNotNull(authToken);
        assertEquals("testUser", authDAO.getUsername(authToken));
    }

    @Test
    void testCreateAuthTokenFailure() {
        assertThrows(DataAccessException.class, () -> {
            String token = new SqlAuthDAO().createAuthToken("");
            assertNull(token);
        });
    }

    @Test
    void testDeleteAuthTokenSuccess() throws DataAccessException {
        String authToken = authDAO.createAuthToken("testUser");
        assertEquals("testUser", authDAO.getUsername(authToken));
        authDAO.deleteAuthToken(authToken);
        assertNull(authDAO.getUsername(authToken));
    }

    @Test
    void testDeleteAuthTokenFailure() throws DataAccessException, SQLException {
        String sql = "DROP TABLE auth";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
        assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuthToken(null);  // Null token should cause failure
        });
        DatabaseManager.createTables();
    }

    @Test
    void testGetSizeSuccess() throws DataAccessException {
        authDAO.createAuthToken("testUser");
        authDAO.createAuthToken("testUser2");
        assertEquals(2, authDAO.getSize());
    }

    @Test
    void testGetSizeFailure() throws DataAccessException, SQLException {
        String sql = "DROP TABLE auth";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        }
        assertThrows(DataAccessException.class, () -> {
            new SqlAuthDAO().getSize();  // Null connection should cause failure
        });
        DatabaseManager.createTables();
    }

    @Test
    void testVerifyAuthTokenSuccess() throws DataAccessException {
        String authToken = authDAO.createAuthToken("testUser");
        assertTrue(authDAO.verifyAuthToken(authToken));
    }

    @Test
    void testVerifyAuthTokenFailure() throws DataAccessException {
        assertFalse(authDAO.verifyAuthToken("invalid_token"));
    }
}
