package model;

import chess.ChessGame;

import java.util.HashSet;
import java.util.Objects;

public class Game {

    private String gameID;
    private String whiteUsername;
    private String blackUsername;
    private HashSet<String> spectators;
    private String gameName;
    private ChessGame game;

    public Game() {
        this.gameID = null;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.spectators = new HashSet<>();
        this.gameName = "";
        this.game = new ChessGame();
    }

    public Game(String gameID, String whiteUsername, String blackUsername, HashSet<String> spectators, String gameName, ChessGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.spectators = spectators;
        this.gameName = gameName;
        this.game = game;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public HashSet<String> getSpectators() {
        return spectators;
    }

    public void addSpectator(String spectator) {
        this.spectators.add(spectator);
    }

    public boolean isSpectator(String spectator) {
        return this.spectators.contains(spectator);
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game1 = (Game) o;
        return Objects.equals(gameID, game1.gameID) && Objects.equals(whiteUsername, game1.whiteUsername) && Objects.equals(blackUsername, game1.blackUsername) && Objects.equals(spectators, game1.spectators) && Objects.equals(gameName, game1.gameName) && Objects.equals(game, game1.game);
    }

    public int generateGameID() {
        // Generate a random 10 digit number
        return (int) (Math.random() * 1000000000L);
    }

    public boolean isOver() {
        return getGame().getTeamTurn() == ChessGame.TeamColor.FINISHED;
    }

    /**
     * Sets the game to be over.
     */
    public void setOver() {
        getGame().setTeamTurn(ChessGame.TeamColor.FINISHED);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, spectators, gameName, game);
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameID='" + gameID + '\'' +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", spectators=" + spectators +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }
}
