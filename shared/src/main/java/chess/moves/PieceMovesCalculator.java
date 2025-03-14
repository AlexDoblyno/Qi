package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

/**
 * An interface for calculating moves a
 * piece can make
 * <p>
 * To be implemented by each type of piece
 */
public interface PieceMovesCalculator {

    /**
     * Checks if position is in the board
     *
     * @param row Row of position
     * @param col Column of position
     * @return True if position is in board
     */
    default boolean inBoard(int row, int col) {
        boolean result = true;
        if ((row < 1) || (row > 8)) {
            result = false;
        } else if ((col < 1) || (col > 8)) {
            result = false;
        }
        return result;
    }

    /**
     * Generic function to be used for finding all
     * possible moves
     *
     * @param board         Board being played on
     * @param startPosition Starting position for move
     * @param myPosition    Current position when finding more moves
     * @param rowChange     Direction to change row for move
     * @param colChange     Direction to change col for move
     * @param recursive     Whether to call recursively
     * @param validMoves    List of moves to add to
     */
    default void getValidMoves(ChessBoard board, ChessPosition startPosition, ChessPosition myPosition,
                               int rowChange, int colChange, boolean recursive, Collection<ChessMove> validMoves) {

        int nextRow = myPosition.getRow() + rowChange;
        int nextCol = myPosition.getColumn() + colChange;
        if (inBoard(nextRow, nextCol)) {
            ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
            ChessPiece nextSquarePiece = board.getPiece(nextPosition);
            if (nextSquarePiece == null) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
                if (recursive) {
                    getValidMoves(board, startPosition, nextPosition, rowChange, colChange, recursive, validMoves);
                }
            } else if (nextSquarePiece.getTeamColor() != (board.getPiece(startPosition)).getTeamColor()) {
                ChessMove move = new ChessMove(startPosition, nextPosition, null);
                validMoves.add(move);
            }
        }
    }

    /**
     * Function for finding all moves
     * Will be overridden by implementing classes
     *
     * @param board      Board being played on
     * @param myPosition Position of piece
     * @return Collection of moves piece can make
     */
    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}

