package chess.pieces;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMove extends PieceMove {
    private int LEFT = -1;
    private int RIGHT = 1;

    @Override
    public Collection<ChessMove> movePiece(ChessBoard board, ChessPosition startPos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPiece piece = board.getPiece(startPos);
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        int forward;
        int startRow;
        int promRow;

        if (teamColor == ChessGame.TeamColor.BLACK) {
            forward = -1;
            startRow = 7;
            promRow = 1;
        }
        else {
            forward = 1;
            startRow = 2;
            promRow = 8;
        }

        int currRow = startPos.getRow();
        int currCol = startPos.getColumn();
        int nextRow = startPos.getRow() + forward;

        ChessPosition advancePos = new ChessPosition(nextRow, currCol);
        ChessMove moveForward = new ChessMove(startPos, advancePos, null);

        if (!isOutOfBounds(moveForward) && !isCollision(board, moveForward)) {
            if (nextRow == promRow) {
                moves.addAll(createPromotion(startPos, advancePos));
            }
            else {
                if (currRow == startRow) {
                    ChessPosition advancePosTwo = new ChessPosition(nextRow + forward, currCol);
                    ChessMove moveForwardTwo = new ChessMove(startPos, advancePosTwo, null);
                    if (!isOutOfBounds(moveForwardTwo) && !isCollision(board, moveForwardTwo)) {
                        moves.add(moveForwardTwo);
                    }
                }
                moves.add(moveForward);
            }
        }

        ArrayList<ChessMove> attackMove = new ArrayList<>();

        ChessPosition attackLeftPos = new ChessPosition(nextRow, currCol + LEFT);
        ChessPosition attackRightPos = new ChessPosition(nextRow, currCol + RIGHT);

        ChessMove attackLeft = new ChessMove(startPos, attackLeftPos, null);
        ChessMove attackRight = new ChessMove(startPos, attackRightPos, null);

        attackMove.add(attackRight);
        attackMove.add(attackLeft);

        for (ChessMove move : attackMove) {
            ChessPosition endPos = move.getEndPosition();
            if (!isOutOfBounds(move) && isAttack(board, move)) {
                if (endPos.getRow() == promRow) {
                    moves.addAll(createPromotion(startPos, endPos));
                } else {
                    moves.add(move);
                }
            }
        }

        return moves;
    }
    private Collection<ChessMove> createPromotion(ChessPosition startPos, ChessPosition endPos) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));

        return moves;
    }
}
