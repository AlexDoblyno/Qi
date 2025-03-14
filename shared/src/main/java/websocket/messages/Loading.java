package websocket.messages;

import chess.ChessGame;

public class Loading extends ServerMessage {
    public ChessGame game;
    public ChessGame.TeamColor color;

    public Loading(ServerMessageType type, ChessGame game, ChessGame.TeamColor color) {
        super(type);
        this.game = game;
        this.color = color;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessGame.TeamColor getColor() {
        return color;
    }
}
