package ui.GameplayHandlers;

import chess.ChessGame;

public class GRedrawHandler extends GameHandler {

    private final ChessGame.TeamColor teamColor;

    public GRedrawHandler(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void redraw(ChessGame game) {
        cbp.printBoard(game.getBoard(), teamColor);
    }
}
