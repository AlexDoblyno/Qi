package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMove extends PieceMove {
    @Override
    public Collection<ChessMove> movePiece(ChessBoard board, ChessPosition startPos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        for (int[] move : VALID_KNIGHT_MOVES) {
            moves.addAll(findValidMoves(board, startPos, null, move, false));
        }

        return moves;
    }
}
