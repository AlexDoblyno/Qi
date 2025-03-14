package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType.ROOK;
import static java.lang.Math.abs;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard;
    private TeamColor turnTeam;

    private boolean gameOver;

    // Variables for special moves
    private Boolean blackKingMoved = false;
    private Boolean whiteKingMoved = false;
    private Boolean blackLeftRookMoved = false;
    private Boolean whiteLeftRookMoved = false;
    private Boolean blackRightRookMoved = false;
    private Boolean whiteRightRookMoved = false;
    private ChessPosition pawnMovedTwo = null;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        turnTeam = TeamColor.WHITE;
        gameOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnTeam = team;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null || gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> potentialMoves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor team = gameBoard.getPiece(startPosition).getTeamColor();

        // Add special moves
        switch (gameBoard.getPiece(startPosition).getPieceType()) {
            case PAWN:
                // En Passant move check
                if (pawnMovedTwo != null) {
                    if (pawnMovedTwo.getRow() == startPosition.getRow() && abs(
                            pawnMovedTwo.getColumn() - startPosition.getColumn()) < 2) {
                        potentialMoves.add(new ChessMove(startPosition, new ChessPosition(
                                (gameBoard.getPiece(startPosition)
                                        .getTeamColor() == TeamColor.BLACK ? (pawnMovedTwo.getRow() - 1) :
                                        (pawnMovedTwo.getRow() + 1)),
                                pawnMovedTwo.getColumn()), null));
                    }
                }
                break;
            case KING:
                // Castling move check
                if (isInCheck(team)) {
                    break;
                }
                if (gameBoard.getPiece(startPosition).getTeamColor() == TeamColor.WHITE) {
                    if (!whiteLeftRookMoved && !whiteKingMoved && (gameBoard.getPiece(
                            new ChessPosition(1, 2)) == null) && (gameBoard.getPiece(
                            new ChessPosition(1, 3)) == null) && (gameBoard.getPiece(new ChessPosition(1,
                            4)) == null) && 1 == startPosition.getRow() && 5 == startPosition.getColumn() && gameBoard.getPiece(
                            new ChessPosition(1, 1)).getPieceType() == ROOK && !isInDanger(team,
                            new ChessPosition(1, 4))) {
                        potentialMoves.add(new ChessMove(startPosition, new ChessPosition(1, 3), null));
                    }
                    if (!whiteRightRookMoved && !whiteKingMoved && gameBoard.getPiece(
                            new ChessPosition(1, 6)) == null && gameBoard.getPiece(new ChessPosition(1,
                            7)) == null && 1 == startPosition.getRow() && 5 == startPosition.getColumn() && gameBoard.getPiece(
                            new ChessPosition(1, 8)).getPieceType() == ROOK && !isInDanger(team,
                            new ChessPosition(1, 6))) {
                        potentialMoves.add(new ChessMove(startPosition, new ChessPosition(1, 7), null));
                    }
                } else {
                    if (!blackLeftRookMoved && !blackKingMoved && (gameBoard.getPiece(
                            new ChessPosition(8, 2)) == null) && (gameBoard.getPiece(
                            new ChessPosition(8, 3)) == null) && (gameBoard.getPiece(new ChessPosition(8,
                            4)) == null) && 8 == startPosition.getRow() && 5 == startPosition.getColumn() && gameBoard.getPiece(
                            new ChessPosition(8, 1)).getPieceType() == ROOK && !isInDanger(team,
                            new ChessPosition(8, 4))) {
                        potentialMoves.add(new ChessMove(startPosition, new ChessPosition(8, 3), null));
                    }
                    if (!blackRightRookMoved && !blackKingMoved && gameBoard.getPiece(
                            new ChessPosition(8, 6)) == null && gameBoard.getPiece(new ChessPosition(8,
                            7)) == null && 8 == startPosition.getRow() && 5 == startPosition.getColumn() && gameBoard.getPiece(
                            new ChessPosition(8, 8)).getPieceType() == ROOK && !isInDanger(team,
                            new ChessPosition(8, 6))) {
                        potentialMoves.add(new ChessMove(startPosition, new ChessPosition(8, 7), null));
                    }
                }
                break;
        }

        // Test if each potential move is valid
        for (ChessMove move : potentialMoves) {
            ChessPiece capturedPiece = gameBoard.doMove(move);

            // If move puts king in check it is not valid
            if (!isInCheck(team)) {
                validMoves.add(move);
            }

            // Undo move after check
            gameBoard.undoMove(move, capturedPiece);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turnTeam) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        // This needs to be set every move so En Passant is only valid the turn after
        pawnMovedTwo = null;

        checkMove(move);

        gameBoard.doMove(move);
        turnTeam = (turnTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> enemyPositions = gameBoard.getPieces(
                (teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK);
        for (ChessPosition position : enemyPositions) {
            if (gameBoard.getPiece(position).canKillKing(gameBoard, position)) {
                return true;
            }
        }
        return false;
    }

    // Check if the piece of teamColor in position piecePosition is in danger (used for castling)
    public boolean isInDanger(TeamColor teamColor, ChessPosition piecePosition) {
        Collection<ChessPosition> enemyPositions = gameBoard.getPieces(
                (teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK);
        for (ChessPosition position : enemyPositions) {
            if (gameBoard.getPiece(position).canKillPiece(gameBoard, position, piecePosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> team = gameBoard.getPieces(teamColor);
        for (ChessPosition position : team) {
            Collection<ChessMove> moves = validMoves(position);
            for (ChessMove move : moves) {
                ChessPiece capturedPiece = gameBoard.doMove(move);
                if (!isInCheck(teamColor)) {
                    gameBoard.undoMove(move, capturedPiece);
                    return false;
                } else {
                    gameBoard.undoMove(move, capturedPiece);
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> team = gameBoard.getPieces(teamColor);
        for (ChessPosition position : team) {
            Collection<ChessMove> moves = validMoves(position);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
        blackKingMoved = false;
        whiteKingMoved = false;
        blackLeftRookMoved = false;
        whiteLeftRookMoved = false;
        blackRightRookMoved = false;
        whiteRightRookMoved = false;
        pawnMovedTwo = null;
    }

    // Used to set special moves variables (mainly to keep track of if kings and rooks have moved, as well as keep
    // track of the pawn that just moved two)
    private void checkMove(ChessMove move) {
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        switch (piece.getPieceType()) {
            case PAWN:
                if (abs(move.getStartPosition().getRow() - move.getEndPosition().getRow()) == 2) {
                    pawnMovedTwo = move.getEndPosition();
                }
                break;
            case ROOK:
                if (piece.getTeamColor() == TeamColor.BLACK && move.getStartPosition().getRow() == 8) {
                    if (move.getStartPosition().getColumn() == 1) {
                        blackLeftRookMoved = true;
                    } else if (move.getStartPosition().getColumn() == 8) {
                        blackRightRookMoved = true;
                    }
                } else if (piece.getTeamColor() == TeamColor.WHITE && move.getStartPosition().getRow() == 1) {
                    if (move.getStartPosition().getColumn() == 1) {
                        whiteLeftRookMoved = true;
                    } else if (move.getStartPosition().getColumn() == 8) {
                        whiteRightRookMoved = true;
                    }
                }
                break;
            case KING:
                if (piece.getTeamColor() == TeamColor.BLACK && move.getStartPosition()
                        .getRow() == 8 && move.getStartPosition().getColumn() == 5) {
                    blackKingMoved = true;
                } else if (piece.getTeamColor() == TeamColor.WHITE && move.getStartPosition()
                        .getRow() == 1 && move.getStartPosition().getColumn() == 5) {
                    whiteKingMoved = true;
                }
                break;
            default:
                break;
        }
    }

    public Boolean isOver() {
        if (isInStalemate(TeamColor.BLACK) || isInStalemate(TeamColor.WHITE)
            || (isInCheckmate(TeamColor.BLACK) && turnTeam.equals(TeamColor.BLACK))
            || (isInCheckmate(TeamColor.WHITE) && turnTeam.equals(TeamColor.WHITE))) {
            gameOver = true;
        }
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE, BLACK
    }
}
