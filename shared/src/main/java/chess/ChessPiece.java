package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import chess.pieces.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;
    private final Collection<ChessMove> moves = new ArrayList<>();

    private final BishopMove bishopMove = new BishopMove();
    private final KingMove kingMove = new KingMove();
    private final KnightMove knightMove = new KnightMove();
    private final PawnMove pawnMove = new PawnMove();
    private final QueenMove queenMove = new QueenMove();
    private final RookMove rookMove = new RookMove();

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType, moves);
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
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> move = new ArrayList<>();

        switch (pieceType) {
            case BISHOP -> move.addAll(bishopMove.movePiece(board, myPosition));
            case KING -> move.addAll(kingMove.movePiece(board, myPosition));
            case KNIGHT -> move.addAll(knightMove.movePiece(board, myPosition));
            case PAWN -> move.addAll(pawnMove.movePiece(board, myPosition));
            case QUEEN -> move.addAll(queenMove.movePiece(board, myPosition));
            case ROOK -> move.addAll(rookMove.movePiece(board, myPosition));
        }
        return move;
    }
}
