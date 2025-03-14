package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition capturedPosition = null;

    ChessPiece[][] squares;

    public ChessBoard() {
        squares = new ChessPiece[8][8];
    }

    public ChessPiece doMove(ChessMove move) {
        ChessPiece piece;
        ChessPiece capturedPiece = getPiece(move.getEndPosition());
        capturedPosition = move.getEndPosition();
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(this.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
        } else {
            piece = this.getPiece(move.getStartPosition());
        }

        // Check if En Passant or Castling
        switch (this.getPiece(move.getStartPosition()).getPieceType()) {
            case PAWN:
                // If En Passant capture make sure to erase piece in capture position
                if (abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 0 && this.getPiece(
                        move.getEndPosition()) == null) {
                    capturedPosition = new ChessPosition(move.getStartPosition().getRow(),
                            move.getEndPosition().getColumn());
                    capturedPiece = this.getPiece(capturedPosition);
                    this.addPiece(capturedPosition, null);
                }
                break;
            case KING:
                // Castling move (set captured piece to own rook for easier implementation)
                if (abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 1) {
                    if (move.getEndPosition().getColumn() == 3) {
                        capturedPosition = new ChessPosition(
                                (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8), 1);
                        capturedPiece = this.getPiece(capturedPosition);
                        this.addPiece(new ChessPosition((piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8), 4),
                                capturedPiece);
                        this.addPiece(capturedPosition, null);
                    } else if (move.getEndPosition().getColumn() == 7) {
                        capturedPosition = new ChessPosition(
                                (piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8), 8);
                        capturedPiece = this.getPiece(capturedPosition);
                        this.addPiece(new ChessPosition((piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : 8), 6),
                                capturedPiece);
                        this.addPiece(capturedPosition, null);
                    }
                }
                break;
            default:
                break;
        }

        this.addPiece(move.getEndPosition(), piece);
        this.addPiece(move.getStartPosition(), null);
        return capturedPiece;
    }

    public void undoMove(ChessMove move, ChessPiece capturedPiece) {
        ChessPiece piece = this.getPiece(move.getEndPosition());
        this.addPiece(move.getStartPosition(), piece);
        this.addPiece(move.getEndPosition(), null);
        this.addPiece(capturedPosition, capturedPiece);

        // If move was Castling make sure to erase rook
        if (piece.getPieceType() == ChessPiece.PieceType.KING && abs(
                move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) > 1) {
            if (move.getEndPosition().getColumn() == 3) {
                this.addPiece(new ChessPosition(move.getStartPosition().getRow(), 4), null);
            } else {
                this.addPiece(new ChessPosition(move.getStartPosition().getRow(), 6), null);
            }
        }
    }

    // Used to get the positions of all pieces on a team (used in calculating check)
    public Collection<ChessPosition> getPieces(ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] != null && teamColor == squares[i][j].getTeamColor()) {
                    pieces.add(new ChessPosition(i + 1, j + 1));
                }
            }
        }
        return pieces;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i > -1; i--) {
            sb.append("|");
            for (int j = 0; j < 8; j++) {
                sb.append(pieceString(squares[i][j]));
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String pieceString(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case ROOK -> "R";
                case KNIGHT -> "N";
                case BISHOP -> "B";
                case QUEEN -> "Q";
                case KING -> "K";
                case PAWN -> "P";
            };
        } else {
            return switch (piece.getPieceType()) {
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case QUEEN -> "q";
                case KING -> "k";
                case PAWN -> "p";
            };
        }
    }
}
