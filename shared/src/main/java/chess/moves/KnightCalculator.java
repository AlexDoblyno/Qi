package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for calculating moves a
 * Knight can make
 */
public class KnightCalculator implements PieceMovesCalculator {

    /**
     * Function for finding all moves a
     * Knight can make
     *
     * @param board      Board being played on
     * @param myPosition Position of piece
     * @return Collection of moves piece can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, -2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 2, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 2, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, 2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 2, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -2, 1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -2, -1, false, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, -2, false, validMoves);
        return validMoves;
    }
}
