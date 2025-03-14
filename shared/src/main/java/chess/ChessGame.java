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
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.teamTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null; // No piece at the given position
        }

        Collection<ChessMove> allMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            // Save the state before the move
            ChessPiece endPiece = board.getPiece(move.getEndPosition());

            // Apply the move
            board.movePiece(move);

            // Check if the move puts the current player's king in check
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            // Undo the move
            board.addPiece(move.getStartPosition(), piece);
            board.addPiece(move.getEndPosition(), null);
            board.addPiece(move.getEndPosition(), endPiece);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        if (startPiece == null) {
            throw new InvalidMoveException("No piece at the start position");
        }

        if (startPiece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It's not this piece's turn");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves != null && validMoves.contains(move)) {
            ChessPiece initialPiece = board.getPiece(move.getStartPosition());
            ChessPiece endPiece = board.getPiece(move.getEndPosition());

            board.movePiece(move);

            if (isInCheck(teamTurn)) {
                board.addPiece(move.getStartPosition(), initialPiece);
                board.addPiece(move.getEndPosition(), endPiece);

                throw new InvalidMoveException("Invalid move: would leave the king in check");
            }
            teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        } else {
            throw new InvalidMoveException("Invalid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor, board);
        return isUnderAttack(kingPosition, teamColor, board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
        return !hasValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;
        return !hasValidMoves(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKingPosition(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
    }

    private boolean isUnderAttack(ChessPosition position, TeamColor teamColor, ChessBoard board) {
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(position)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);
                    if (moves != null && !moves.isEmpty()) {
                        for (ChessMove move : moves) {
                            ChessPiece initialPiece = board.getPiece(move.getStartPosition());
                            ChessPiece endPiece = board.getPiece(move.getEndPosition());

                            board.movePiece(move);
                            boolean stillInCheck = isInCheck(teamColor);
                            board.addPiece(move.getStartPosition(), initialPiece);
                            board.addPiece(move.getEndPosition(), endPiece);

                            if (!stillInCheck) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
