package chess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> moves = new ArrayList<>();

        if (piece == null) {
            return moves;
        }

        switch (piece.getPieceType()) {
            case PAWN:
                moves = addPawnMoves(board, myPosition);
                break;
            case ROOK:
                moves = addRookMoves(board, myPosition);
                break;
            case BISHOP:
                moves = addBishopMoves(board, myPosition);
                break;
            case KNIGHT:
                moves = addKnightMoves(board, myPosition);
                break;
            case QUEEN:
                moves = addQueenMoves(board, myPosition);
                break;
            case KING:
                moves = addKingMoves(board, myPosition);
                break;
            default:
                throw new IllegalStateException("Unexpected piece type: " + piece);
        }
        return moves;
    }

    private Collection<ChessMove> addPawnMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        //White Logic
        if (getTeamColor() == ChessGame.TeamColor.WHITE) {
            // First move
            if (position.getRow() == 2) {
                ChessPosition endPosition = new ChessPosition(position.getRow() + 1, position.getColumn());
                ChessPiece piece = board.getPiece(endPosition);
                ChessPosition endPosition2 = new ChessPosition(position.getRow() + 2, position.getColumn());
                ChessPiece piece2 = board.getPiece(endPosition2);
                if (piece == null && piece2 == null) {
                    moves.add(new ChessMove(position, endPosition2, null));
                }
            }


            // Any move
            ChessPosition endPosition = new ChessPosition(position.getRow() + 1, position.getColumn());
            moves.addAll(pawnMoves(board, position, endPosition, 8));

            // Attack
            ChessPosition attackPosition = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
            moves.addAll(pawnAttack(board, position, attackPosition));
            ChessPosition attackPosition2 = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
            moves.addAll(pawnAttack(board, position, attackPosition2));
        }

        // Black Logic
        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            // First move
            if (position.getRow() == 7) {
                ChessPosition endPosition = new ChessPosition(position.getRow() - 1, position.getColumn());
                ChessPiece piece = board.getPiece(endPosition);
                ChessPosition endPosition2 = new ChessPosition(position.getRow() - 2, position.getColumn());
                ChessPiece piece2 = board.getPiece(endPosition2);
                if (piece == null && piece2 == null) {
                    moves.add(new ChessMove(position, endPosition2, null));
                }
            }

            // Any move
            ChessPosition endPosition = new ChessPosition(position.getRow() - 1, position.getColumn());
            moves.addAll(pawnMoves(board, position, endPosition, 1));

            // Attack
            ChessPosition attackPosition = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
            moves.addAll(pawnAttack(board, position, attackPosition));
            ChessPosition attackPosition2 = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
            moves.addAll(pawnAttack(board, position, attackPosition2));
        }
        return moves;
    }

    private Collection<ChessMove> addRookMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        // Right
        for (int i = 1; i < 9 - position.getColumn(); i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow(), position.getColumn() + i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Left
        for (int i = 1; i < position.getColumn(); i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow(), position.getColumn() - i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Down
        for (int i = 1; i < position.getRow(); i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() - i, position.getColumn());
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Up
        for (int i = 1; i < 9 - position.getRow(); i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() + i, position.getColumn());
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> addBishopMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        //Moving Up and Right
        int moveMax = 8 - Math.max(position.getRow(), position.getColumn());
        for (int i = 1; i <= moveMax; i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() + i, position.getColumn() + i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Moving Down and Right
        moveMax = 8 - Math.max(9 - position.getRow(), position.getColumn());
        for (int i = 1; i <= moveMax; i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() - i, position.getColumn() + i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Moving Left and Up
        moveMax = 8 - Math.max(position.getRow(), 9 - position.getColumn());
        for (int i = 1; i <= moveMax; i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() + i, position.getColumn() - i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        // Moving Left and Down
        moveMax = 8 - Math.max(9 - position.getRow(), 9 - position.getColumn());
        for (int i = 1; i <= moveMax; i++) {
            ChessPosition endPosition = new ChessPosition(position.getRow() - i, position.getColumn() - i);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
            if (piece != null) {
                break;
            }
        }
        return moves;
    }

    private Collection<ChessMove> addKnightMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList();
        int row = position.getRow();
        int col = position.getColumn();

        //Up Right
        moves.addAll(knightMoves(board, position, row + 2, col + 1));
        //Up Left
        moves.addAll(knightMoves(board, position, row + 2, col - 1));
        //Right Up
        moves.addAll(knightMoves(board, position, row + 1, col + 2));
        //Right Down
        moves.addAll(knightMoves(board, position, row - 1, col + 2));
        //Down Right
        moves.addAll(knightMoves(board, position, row - 2, col + 1));
        //Down Left
        moves.addAll(knightMoves(board, position, row - 2, col - 1));
        //Left Up
        moves.addAll(knightMoves(board, position, row + 1, col - 2));
        //Left Down
        moves.addAll(knightMoves(board, position, row - 1, col - 2));
        return moves;
    }

    private Collection<ChessMove> addQueenMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves;
        Collection<ChessMove> rookMoves;
        moves = addBishopMoves(board, position);
        rookMoves = addRookMoves(board, position);
        moves.addAll(rookMoves);
        return moves;
    }

    private Collection<ChessMove> addKingMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        //Up Right
        moves.addAll(knightMoves(board, position, row + 1, col + 1));
        //Up Left
        moves.addAll(knightMoves(board, position, row + 1, col - 1));
        //Down Right
        moves.addAll(knightMoves(board, position, row - 1, col + 1));
        //Down Left
        moves.addAll(knightMoves(board, position, row - 1, col - 1));
        //Up
        moves.addAll(knightMoves(board, position, row + 1, col));
        //Down
        moves.addAll(knightMoves(board, position, row - 1, col));
        //Right
        moves.addAll(knightMoves(board, position, row, col + 1));
        //Left
        moves.addAll(knightMoves(board, position, row, col - 1));
        return moves;
    }

    public Collection<ChessMove> pawnAttack(ChessBoard board, ChessPosition position, ChessPosition attack) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (attack.checkBounds()) {
            ChessPiece piece2 = board.getPiece(attack);
            if (piece2 != null && getTeamColor() != piece2.getTeamColor() && attack.getRow() == 1) {
                moves.add(new ChessMove(position, attack, PieceType.KNIGHT));
                moves.add(new ChessMove(position, attack, PieceType.ROOK));
                moves.add(new ChessMove(position, attack, PieceType.QUEEN));
                moves.add(new ChessMove(position, attack, PieceType.BISHOP));
            } else if (piece2 != null && getTeamColor() != piece2.getTeamColor()) {
                moves.add(new ChessMove(position, attack, null));
            }
        }
        return moves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition position, ChessPosition endPosition, int rowComp) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(endPosition);
        if (piece == null && endPosition.getRow() == rowComp) {
            moves.add(new ChessMove(position, endPosition, PieceType.KNIGHT));
            moves.add(new ChessMove(position, endPosition, PieceType.ROOK));
            moves.add(new ChessMove(position, endPosition, PieceType.QUEEN));
            moves.add(new ChessMove(position, endPosition, PieceType.BISHOP));
        } else if (piece == null) {
            moves.add(new ChessMove(position, endPosition, null));
        }
        return moves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition position, int tmprow, int tmpcol) {
        ArrayList<ChessMove> moves = new ArrayList();
        if (tmprow >= 1 && tmpcol >= 1 && tmprow <= 8 && tmpcol <= 8) {
            ChessPosition endPosition = new ChessPosition(tmprow, tmpcol);
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null || piece.getTeamColor() != getTeamColor()) {
                moves.add(new ChessMove(position, endPosition, null));
            }
        }
        return moves;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", teamColor=" + teamColor +
                "}\n";
    }
}


