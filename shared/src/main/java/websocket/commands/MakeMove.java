package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    ChessMove move;

    public MakeMove(CommandType type, String authToken, int gameID, ChessMove move) {
        super(type, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return this.move;
    }
}
