package dataaccess.DAO;

import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import model.UserData;

public class MemoryUserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {}

    public UserData createUser(UserData user) {
        String hashedPassword = hashPassword(user.password());

        UserData hashedUser = new UserData(user.username(),hashedPassword, user.email());

        users.put(user.username(), hashedUser);
        return hashedUser;
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear() {
        users.clear();
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            md.update(password.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}