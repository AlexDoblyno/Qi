package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MovesCalculator {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return switch (board.getPiece(myPosition).getPieceType()) {
            case BISHOP -> bishopMoves(board, myPosition);
            case ROOK -> rookMoves(board, myPosition);
            case KING -> kingMoves(board, myPosition);
            case QUEEN -> queenMoves(board, myPosition);
            case KNIGHT -> knightMoves(board, myPosition);
            case PAWN -> pawnMoves(board, myPosition);
            default -> null;
        };
    }

    private static Collection<ChessMove> linearMoves(Boolean iterative, ChessBoard board, ChessPosition myPosition,
                                                     int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction : directions) {
            int dy = direction[0];
            int dx = direction[1];
            int depth = 1;
            do {
                ChessPosition endPosition = new ChessPosition(myPosition.getRow() + dy * depth,
                        myPosition.getColumn() + dx * depth);
                if (endPosition.outsideBounds()) {
                    break;
                }

                if (board.getPiece(endPosition) == null) {
                    moves.add(new ChessMove(myPosition, endPosition, null));
                } else if (board.getPiece(endPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, endPosition, null));
                    break;
                } else {
                    break;
                }

                depth++;
            } while (iterative);
        }

        return moves;
    }

    private static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
        return linearMoves(true, board, myPosition, directions);
    }

    private static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return linearMoves(true, board, myPosition, directions);
    }

    private static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        Collection<ChessMove> moves = linearMoves(false, board, myPosition, directions);
        return moves;
    }

    private static Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        return linearMoves(true, board, myPosition, directions);
    }

    private static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {{2, 1}, {-2, 1}, {2, -1}, {-2, -1}, {1, -2}, {-1, -2}, {1, 2}, {-1, 2}};
        return linearMoves(false, board, myPosition, directions);
    }

    private static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int direction = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) ? -1 : 1;

        ChessPosition one = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (!one.outsideBounds() && board.getPiece(one) == null) {
            if (one.getRow() == 1 || one.getRow() == 8) {
                moves.add(new ChessMove(myPosition, one, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, one, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, one, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, one, ChessPiece.PieceType.QUEEN));
            } else {
                moves.add(new ChessMove(myPosition, one, null));
                if ((direction == -1 && myPosition.getRow() == 7) || (direction == 1 && myPosition.getRow() == 2)) {
                    ChessPosition two = new ChessPosition(myPosition.getRow() + direction * 2, myPosition.getColumn());
                    if (board.getPiece(two) == null) {
                        moves.add(new ChessMove(myPosition, two, null));
                    }
                }
            }
        }

        ChessPosition left = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        if (!left.outsideBounds() && board.getPiece(left) != null && board.getPiece(left)
                .getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            if (left.getRow() == 1 || left.getRow() == 8) {
                moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.QUEEN));
            } else {
                moves.add(new ChessMove(myPosition, left, null));
            }
        }

        ChessPosition right = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        if (!right.outsideBounds() && board.getPiece(right) != null && board.getPiece(right)
                .getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
            if (right.getRow() == 1 || right.getRow() == 8) {
                moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.ROOK));
                moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.KNIGHT));
                moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.QUEEN));
            } else {
                moves.add(new ChessMove(myPosition, right, null));
            }
        }

        return moves;
    }
}
