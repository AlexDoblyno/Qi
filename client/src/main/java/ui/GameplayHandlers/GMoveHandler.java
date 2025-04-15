package ui.GameplayHandlers;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ResponseException;
import model.AuthToken;
import ui.Printer;
import wsfacade.WSFacade;

public class GMoveHandler extends OnlineGameHandler {

    public GMoveHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void extractMoveAndSend(Integer gameID, String[] args) throws ResponseException {
        if (args.length < 2) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Invalid number of arguments");
            new GHelpHandler().help();
            return;
        }

        // Convert from letter-number to x-y
        var position = args[1];
        var y1 = position.charAt(0) - 'a' + 1;
        var x1 = position.charAt(1) - '1' + 1;
        var y2 = position.charAt(2) - 'a' + 1;
        var x2 = position.charAt(3) - '1' + 1;

        var cPosition = new ChessPosition(x1, y1);
        var cPosition2 = new ChessPosition(x2, y2);

        ChessPiece.PieceType pieceType = null;
        if (args.length == 3) {
            //Check if promotion
            switch (args[2]) {
                case "q" -> pieceType = ChessPiece.PieceType.QUEEN;
                case "r" -> pieceType = ChessPiece.PieceType.ROOK;
                case "b" -> pieceType = ChessPiece.PieceType.BISHOP;
                case "n" -> pieceType = ChessPiece.PieceType.KNIGHT;
                default -> {
                    p.reset();
                    p.setColor(Printer.Color.RED);
                    p.println("Invalid promotion");
                    new GHelpHandler().help();
                    return;
                }
            }
        }

        var move = new ChessMove(cPosition, cPosition2, pieceType);
        move(gameID, move);
    }

    public void move(Integer gameID, ChessMove move) throws ResponseException {
        wsFacade.sendMove(authToken, gameID, move);
    }
}
