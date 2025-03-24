package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class BoardPrinter {

    private BoardPrinter() { }

    public static void printBoard(ChessBoard board, ChessGame.TeamColor bottomColor, Collection<ChessPosition> highlight) {
        boolean whiteOnBottom = (bottomColor == ChessGame.TeamColor.WHITE);
        // Column labels (a-h) for the board, queen on the right for black perspective
        String[] columnLabels = {"\u2003A ", "\u2003B ", "\u2003C ", "\u2003D ", "\u2003E ", "\u2003F ",
                "\u2003G ", "\u2003H "};

        System.out.println(EscapeSequences.ERASE_SCREEN);

        int startRow = whiteOnBottom ? 7 : 0;
        int endRow = whiteOnBottom ? 0 : 7;
        int rowStep = whiteOnBottom ? -1 : 1;
        int startCol = whiteOnBottom ? 0 : 7;
        int endCol = whiteOnBottom ? 7 : 0;
        int colStep = whiteOnBottom ? 1 : -1;

        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY);
        for (int i = startCol; i - colStep != endCol; i += colStep) {
            System.out.print(columnLabels[i]);
        }
        System.out.println(EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
        for (int i = startRow; i - rowStep != endRow; i += rowStep) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + "\u2003" + (i + 1) + " ");
            if (i % 2 == 0) {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = (j % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                    if (highlight != null && highlight.contains(new ChessPosition(i+1, j+1))) {
                        backgroundColor = (j % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREEN :
                                EscapeSequences.SET_BG_COLOR_GREEN;
                    }
                    System.out.print(backgroundColor + getPieceString(board.getPiece(new ChessPosition(i+1, j+1))));
                }
            } else {
                for (int j = startCol; j - colStep != endCol; j += colStep) {
                    String backgroundColor = (j % 2 != 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                            EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                    if (highlight != null && highlight.contains(new ChessPosition(i+1, j+1))) {
                        backgroundColor = (j % 2 != 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREEN :
                                EscapeSequences.SET_BG_COLOR_GREEN;
                    }
                    System.out.print(backgroundColor + getPieceString(board.getPiece(new ChessPosition(i+1, j+1))));
                }
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + " " + (i + 1) + "\u2003" + EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY);
        for (int i = startCol; i - colStep != endCol; i += colStep) {
            System.out.print(columnLabels[i]);
        }
        System.out.println(EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);

    }

    public static void printHighlightedMoves(ChessBoard board, ChessGame.TeamColor bottomColor, Collection<ChessMove> moves) {
        if (moves != null) {
            Collection<ChessPosition> positions = new ArrayList<>();
            for (ChessMove move : moves) {
                positions.add(move.getStartPosition());
                positions.add(move.getEndPosition());
            }

            printBoard(board, bottomColor, positions);
        } else {
            System.out.println("No moves found");
        }
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        } else {
            return switch (piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        }
    }
}
