package dataaccess;

public interface AuthDAO {
    void clear() throws DataAccessException;

    public String getUsername(String authToken) throws DataAccessException;

    String createAuthToken(String username) throws DataAccessException;

    void deleteAuthToken(String authToken) throws DataAccessException;

    Integer getSize() throws DataAccessException;

    public Boolean verifyAuthToken(String authToken) throws DataAccessException;
}
