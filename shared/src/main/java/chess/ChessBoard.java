package chess;

import java.util.Arrays;
import java.util.Objects;


/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {
        
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }
        for (ChessGame.TeamColor color : ChessGame.TeamColor.values()) {
            initPieces(color);
        };
    }

    public void movePiece(ChessMove move) {

        int initialRow = move.getStartPosition().getRow() - 1;
        int lastRow = move.getEndPosition().getRow() - 1;

        int initialCol = move.getStartPosition().getColumn() - 1;
        int lastCol = move.getEndPosition().getColumn() - 1;

        ChessPiece initialPiece = getPiece(move.getStartPosition());

        if (initialPiece.getPieceType() != ChessPiece.PieceType.PAWN)
            squares[lastRow][lastCol] = initialPiece;
        else if (move.getPromotionPiece() != null)
            squares[lastRow][lastCol] = new ChessPiece(initialPiece.getTeamColor(), move.getPromotionPiece());
        else
            squares[lastRow][lastCol] = initialPiece;

        squares[initialRow][initialCol] = null;
    }

    private void initPieces(ChessGame.TeamColor teamColor) {

        int frontRow = teamColor == ChessGame.TeamColor.BLACK ? 6 : 1;
        int backRow = teamColor == ChessGame.TeamColor.BLACK ? 7 : 0;

        ChessPiece bishop = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
        ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPiece knight = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
        ChessPiece pawn = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
        ChessPiece queen = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
        ChessPiece rook = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);

        initPawns(frontRow, pawn);

        initBackRow(backRow, rook, knight, bishop, queen, king);
    }

    private void initPawns(int row, ChessPiece pawn) {
        for (int i = 0; i < 8; i++) {
            squares[row][i] = pawn;
        }
    }

    private void initBackRow(int row, ChessPiece rook, ChessPiece knight, ChessPiece bishop, ChessPiece queen, ChessPiece king) {
        squares[row][0] = rook;
        squares[row][1] = knight;
        squares[row][2] = bishop;
        squares[row][3] = queen;
        squares[row][4] = king;
        squares[row][5] = bishop;
        squares[row][6] = knight;
        squares[row][7] = rook;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
