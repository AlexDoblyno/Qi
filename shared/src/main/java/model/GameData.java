package model;

import chess.ChessGame;

import java.util.Objects;

public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID
                && Objects.equals(game, gameData.game)
                && Objects.equals(gameName, gameData.gameName)
                && Objects.equals(whiteUsername, gameData.whiteUsername)
                && Objects.equals(blackUsername, gameData.blackUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName, game);
    }
}