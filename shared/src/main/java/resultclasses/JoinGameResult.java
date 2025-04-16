package resultclasses;

import chess.ChessGame;

public class JoinGameResult extends Result {

    private ChessGame.TeamColor playerColor;
    private int gameID;

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public JoinGameResult() {
        super();
        this.playerColor = null;
    }

    public JoinGameResult(int status, ChessGame.TeamColor playerColor, int gameID) {
        super(status);
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public JoinGameResult(int status, String message) {
        super(status, message);
    }
}
