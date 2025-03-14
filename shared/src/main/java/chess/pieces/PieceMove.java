package chess.pieces;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMove {
    protected final int[][] ALL_MOVES = {
            {1, 0}, {0, 1}, {-1, 0}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    protected final int[][] VERTICAL_MOVES = {
            {1, 0},
            {-1, 0},
            {0, -1},
            {0, 1}
    };

    protected final int[][] DIAGONAL_MOVES = {
            {1, -1},
            {1, 1},
            {-1, -1},
            {-1, 1}
    };

    protected final int[][] VALID_KNIGHT_MOVES = {
            {2, -1},
            {-2, -1},
            {2, 1},
            {-2, 1},
            {1, -2},
            {1, 2},
            {-1, -2},
            {-1, 2}
    };

    public Collection<ChessMove> movePiece(ChessBoard board, ChessPosition startPos) {return new ArrayList<>();}

    public boolean isOutOfBounds(ChessMove move) {
        int col = move.getEndPosition().getColumn();
        int row = move.getEndPosition().getRow();

        return row > 8 || col > 8 || row < 1 || col < 1;
    }

    protected boolean isCollision(ChessBoard board, ChessMove move) {
        return board.getPiece(move.getEndPosition()) != null;
    }

    protected boolean isAttack(ChessBoard board, ChessMove move) {
        if (isCollision(board, move)) {
            ChessPiece startPiece = board.getPiece(move.getStartPosition());
            ChessPiece endPiece = board.getPiece(move.getEndPosition());

            return startPiece.getTeamColor() != endPiece.getTeamColor();
        }
        return false;
    }

    protected Collection<ChessMove> findValidMoves(ChessBoard board, ChessPosition start, ChessPosition current, int[] direction, boolean recursion) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        if (current == null) {current = start;}

        int newRow = current.getRow() + direction[0];
        int newCol = current.getColumn() + direction[1];
        ChessPosition newPos = new ChessPosition(newRow, newCol);

        ChessMove newMove = new ChessMove(start, newPos, null);

        if (isOutOfBounds(newMove)) {return validMoves;}

        if (isCollision(board, newMove)) {
            if (isAttack(board, newMove)) {
                validMoves.add(newMove);
            }
            return validMoves;
        }

        validMoves.add(newMove);

        if(recursion) {
            validMoves.addAll(findValidMoves(board, start, newPos, direction, true));
        }

        return validMoves;
    }
}
