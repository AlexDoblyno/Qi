package ui;

import models.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// DataCache is meant to hold data that might need to persist between REPL loops or clients.
public class DataCache {
    private String authToken;
    private int currentGameID;
    private List<GameData> gameCache;

    // Empty constructor
    public DataCache() {
        authToken = null;
        currentGameID = 0;
        gameCache = new ArrayList<>();
    }

    public DataCache(String authToken, int gameID, Collection<GameData> gameList) {
        this.authToken = authToken;
        currentGameID = gameID;
        gameCache = new ArrayList<GameData>(gameList);
    }

    public String getAuthToken() {
        return authToken;
    }

