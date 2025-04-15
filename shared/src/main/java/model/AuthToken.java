package model;

import java.util.Objects;
import java.util.UUID;

public class AuthToken {
    private final String authToken;
    private final String username;

    public AuthToken() {
        this.authToken = null;
        this.username = null;
    }

    public AuthToken(String username) {
        this.authToken = UUID.randomUUID().toString();
        this.username = username;
    }

    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken1 = (AuthToken) o;
        return Objects.equals(authToken, authToken1.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken);
    }
}