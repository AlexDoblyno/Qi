package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private boolean teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        TeamColor team = TeamColor.WHITE;
        if (teamTurn) {
            team = TeamColor.BLACK;
        }
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team != TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Verifies that suggested castle move is valid
     *
     * @param myPosition Position of piece being moved
     * @param move       Move that needs checking for validity
     * @param piece      Piece being moved
     * @return True or False whether suggested castle move
     * is valid
     */
    private boolean validCastle(ChessPosition myPosition, ChessMove move, ChessPiece piece) {
        boolean result = false;
        if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING &&
                Math.abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) == 2) {
            int shift = (move.getEndPosition().getColumn() - move.getStartPosition().getColumn()) / 2;
            ChessBoard copyBoard = new ChessBoard(board);
            copyBoard.movePiece(new ChessMove(myPosition,
                    new ChessPosition(myPosition.getRow(), myPosition.getColumn() + shift), null));
            ChessBoard temp = board;
            board = copyBoard;
            if (isInCheck(piece.getTeamColor())) {
                result = true;
            }
            board = temp;
        }
        return result;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        boolean castleCheck;
        ArrayList<ChessMove> moves = null;
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null) {
            moves = (ArrayList<ChessMove>) piece.pieceMoves(board, startPosition);
            for (int i = 0; i < moves.size(); i++) {
                castleCheck = validCastle(startPosition, moves.get(i), piece);
                ChessBoard copyBoard = new ChessBoard(board);
                copyBoard.movePiece(moves.get(i));
                ChessBoard temp = board;
                board = copyBoard;
                if (isInCheck(piece.getTeamColor()) || castleCheck) {
                    moves.remove(i);
                    i--;
                }
                board = temp;
            }
        }
        return moves;
    }

    /**
     * Resets vulnerability to En Passant move
     */
    private void clearEnPassant() {
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == getTeamTurn()) {
                    piece.setSubjectToEnPassant(false);
                }
            }
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        clearEnPassant();
        boolean success = false;
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move) && board.getPiece(move.getStartPosition()).getTeamColor() == getTeamTurn()) {
            success = board.movePiece(move);
        }
        if (!success || isInCheck(getTeamTurn())) {
            throw new InvalidMoveException();
        }
        teamTurn = !teamTurn;
    }

    /**
     * Finds all moves the team can make when in
     * check
     *
     * @param team Which team to collect moves for
     * @return Collection of moves the team can make
     */
    private Collection<ChessMove> teamAllMovesInCheck(ChessGame.TeamColor team) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPosition currPosition;
        ChessPiece currPiece;
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                currPosition = new ChessPosition(r, c);
                currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == team) {
                    validMoves.addAll(validMoves(currPosition));
                }
            }
        }
        return validMoves;
    }

    /**
     * Finds all moves the opposing team can make
     *
     * @param team Which team to collect moves for
     * @return Collection of moves the opposing
     * team can make
     */
    private Collection<ChessMove> teamAllMoves(ChessGame.TeamColor team) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPosition currPosition;
        ChessPiece currPiece;
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                currPosition = new ChessPosition(r, c);
                currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == team) {
                    validMoves.addAll(currPiece.pieceMoves(board, currPosition));
                }
            }
        }
        return validMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean result = false;
        Collection<ChessMove> validMoves;
        ChessPosition currPosition;
        ChessPosition kingPosition = null;
        ChessPiece currPiece;
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                currPosition = new ChessPosition(r, c);
                currPiece = board.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor &&
                        currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = currPosition;
                }
            }
        }

        ChessGame.TeamColor otherTeamColor;
        if (teamColor == TeamColor.WHITE) {
            otherTeamColor = TeamColor.BLACK;
        } else {
            otherTeamColor = TeamColor.WHITE;
        }

        validMoves = teamAllMoves(otherTeamColor);

        for (ChessMove move : validMoves) {
            if (kingPosition != null && move.isEndPosition(kingPosition)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean result = false;
        if (isInCheck(teamColor)) {
            if (teamAllMovesInCheck(teamColor).isEmpty()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        boolean result = false;
        if (!isInCheck(teamColor)) {
            if (teamAllMovesInCheck(teamColor).isEmpty()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        if (board != null) {
            this.board = new ChessBoard(board);
        } else {
            this.board = new ChessBoard();
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
