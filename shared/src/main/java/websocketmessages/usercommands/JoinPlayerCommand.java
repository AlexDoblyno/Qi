package websocketmessages.usercommands;

import chess.ChessGame;
import model.AuthToken;

public class JoinPlayerCommand extends UserGameCommand {
    private Integer gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(AuthToken authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(ChessGame.TeamColor playerColor) {
        this.playerColor = playerColor;
    }
}
