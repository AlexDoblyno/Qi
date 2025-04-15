package ui.GameplayHandlers;

import chess.ChessGame;
import chess.ChessPosition;
import ui.Printer;

public class GHighlightHelper extends GameHandler {

    public GHighlightHelper(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    private final ChessGame.TeamColor teamColor;

    public void extractPositionAndView(ChessGame game, String[] args) {
        if (args.length != 2) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Invalid number of arguments");
            new GHelpHandler().help();
            return;
        }

        // Convert from letter-number to x-y
        var position = args[1];
        var y = position.charAt(0) - 'a' + 1;
        var x = position.charAt(1) - '1' + 1;

        var cPosition = new ChessPosition(x, y);

        highlightMoves(game, cPosition);
    }

    public void highlightMoves(ChessGame game, ChessPosition position) {
        var validMoves = game.validMoves(position);
        cbp.printBoardHighlighted(game.getBoard(), teamColor, position, validMoves);
    }

}
