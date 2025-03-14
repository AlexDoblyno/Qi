package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for calculating moves a
 * Bishop can make
 */
public class BishopCalculator implements PieceMovesCalculator {

    /**
     * Function for finding all moves a
     * Bishop can make
     *
     * @param board      Board being played on
     * @param myPosition Position of piece
     * @return Collection of moves piece can make
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        getValidMoves(board, myPosition, myPosition, 1, 1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, 1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, 1, -1, true, validMoves);
        getValidMoves(board, myPosition, myPosition, -1, -1, true, validMoves);
        return validMoves;
    }
}
