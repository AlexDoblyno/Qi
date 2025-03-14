package server.responses;

import model.GameData;

import java.util.List;

public class ListGamesResponse {
    private List<GameData> games;

    public ListGamesResponse(List<GameData> games) {
        this.games = games;
    }

    public List<GameData> getGames() {
        return games;
    }

}