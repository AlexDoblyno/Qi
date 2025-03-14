package ui;

import chess.*;
import model.GameData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class GameplayUI {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String BORDER_COLOR = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE;
    private static final String WHITE_TILE = SET_BG_COLOR_WHITE;
    private static final String BLACK_TILE = SET_BG_COLOR_BLACK;
    private static final String WHITE_TEAM = SET_TEXT_COLOR_RED;
    private static final String BLACK_TEAM = SET_TEXT_COLOR_BLUE;
    private static final String HIGHLIGHT = SET_BG_COLOR_MAGENTA;

    public static void displayGame(GameData gameData, ChessGame.TeamColor perspective, ChessPosition startPosition) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        ChessBoard board = gameData.game().getBoard();

        Collection<ChessPosition> highlightPositions = new ArrayList<>();
        if (startPosition != null) {
            Collection<ChessMove> validMoves = gameData.game().validMoves(startPosition);
            for (var c : validMoves) {
                highlightPositions.add(c.getEndPosition());
            }
        }
        if (perspective == ChessGame.TeamColor.BLACK) {
            drawHeaders(out, perspective);
            for (int i = 1; i <= 8; i++) {
                drawRow(out, i, board, perspective, highlightPositions);
            }
            drawHeaders(out, perspective);
        } else {
            drawHeaders(out, perspective);
            for (int i = 8; i >= 1; i--) {
                drawRow(out, i, board, perspective, highlightPositions);
            }
            drawHeaders(out, perspective);
        }
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor perspective) {
        String[] headers = {"a", "b", "c", "d", "e", "f", "g", "h"};
        out.print(borderFormat(" "));

        if (perspective.equals(ChessGame.TeamColor.BLACK)) {
            for (int i = 7; i >= 0; i--) {
                out.print(borderFormat(headers[i]));
            }
        } else {
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                out.print(borderFormat(headers[boardCol]));
            }
        }
        out.println("   " + RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    private static String getPieceString(ChessBoard board, int row, int col, boolean printHighlighted) {
        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        String background = ((row + col) % 2 == 0) ? BLACK_TILE : WHITE_TILE;
        if (printHighlighted) {
            background = HIGHLIGHT;
        }
        if (piece != null) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                return WHITE_TEAM + background + " " + getPieceSymbol(piece) + " ";
            } else {
                return BLACK_TEAM + background + " " + getPieceSymbol(piece) + " ";
            }
        } else {
            return background + "   ";
        }
    }

    private static String borderFormat(Object character) {
        return BORDER_COLOR + " " + character + " ";
    }

    private static void drawRow(PrintStream out, int row, ChessBoard board, ChessGame.TeamColor perspective, Collection<ChessPosition> highlightPositions) {
        out.print(borderFormat(row));
        if (perspective.equals(ChessGame.TeamColor.BLACK)) {
            for (int i = 8; i >= 1; i--) {
                ChessPosition compared = new ChessPosition(row, i);
                boolean highlight = false;
                if (highlightPositions.contains(compared)) {
                    highlight = true;
                }
                out.print(getPieceString(board, row, i, highlight));
            }
        } else {
            for (int i = 1; i <= 8; i++) {
                ChessPosition compared = new ChessPosition(row, i);
                boolean highlight = false;
                if (highlightPositions.contains(compared)) {
                    highlight = true;
                }
                out.print(getPieceString(board, row, i, highlight));
            }
        }
        out.print(borderFormat(row) + RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
    }

    private static String getPieceSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
            default -> " ";
        };
    }
}