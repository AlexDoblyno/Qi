package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlUserDAOTests {


    private UserDAO userDAO;

    @BeforeAll
    static void createTables() throws DataAccessException {
        DatabaseManager.createTables();
    }

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        userDAO = new SqlUserDAO();
        userDAO.clear();  // Clear users before each test
    }

    @AfterEach
    void tearDown() throws SQLException, DataAccessException {
        userDAO.clear();  // Clean up after tests
    }

    // Test for creating a user
    @Test
    void testCreateUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password123", "test@example.com");
        assertEquals(1, userDAO.getSize());  // Verify the user count is now 1
    }

    @Test
    void testCreateUserFail() throws DataAccessException {
        userDAO.createUser("testUser", "password123", "test@example.com");

        // Try to create the same user again and check for exception
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser("testUser", "password456", "test2@example.com");
        });
    }

    // Test for getting a user
    @Test
    void testGetUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password123", "test@example.com");
        UserData userData = userDAO.getUser("testUser", "password123");
        assertNotNull(userData);
        assertEquals("testUser", userData.username());
        assertEquals("test@example.com", userData.email());
    }

    @Test
    void testGetUserFail() throws DataAccessException {
        userDAO.createUser("testUser", "password123", "test@example.com");
        UserData userData = userDAO.getUser("testUser", "wrongPassword");
        assertNull(userData);  // Should return null for wrong password
    }

    // Test for getting size of users
    @Test
    void testGetSizeSuccess() throws DataAccessException {
        assertEquals(0, userDAO.getSize());  // Initially, size should be 0
        userDAO.createUser("testUser", "password123", "test@example.com");
        assertEquals(1, userDAO.getSize());  // After adding a user, size should be 1
    }

    @Test
    void testGetSizeFail() throws DataAccessException {
        userDAO.clear();  // Ensure the table is empty
        assertEquals(0, userDAO.getSize());  // Should still return 0
    }

    // Test for verifying a user
    @Test
    void testVerifyUserSuccess() throws DataAccessException {
        userDAO.createUser("testUser", "password123", "test@example.com");
        assertTrue(userDAO.verifyUser("testUser"));  // Should return true for existing user
    }

    @Test
    void testVerifyUserFail() throws DataAccessException {
        assertFalse(userDAO.verifyUser("nonExistentUser"));  // Should return false for non-existing user
    }

    // Test for clearing users
    @Test
    void testClearSuccess() throws DataAccessException {
        userDAO.createUser("testUser1", "password123", "test1@example.com");
        userDAO.createUser("testUser2", "password456", "test2@example.com");

        userDAO.clear();  // Clear the user table
        assertEquals(0, userDAO.getSize());  // Ensure the table is empty
    }
}
