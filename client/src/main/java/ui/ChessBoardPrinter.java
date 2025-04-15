package ui;

import chess.*;

import java.util.Collection;
import java.util.List;

public class ChessBoardPrinter extends Printer {

    public void printBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        List<Integer> rows;
        List<Integer> columns;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            rows = List.of(8, 7, 6, 5, 4, 3, 2, 1);
            columns = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        } else {
            rows = List.of(1, 2, 3, 4, 5, 6, 7, 8);
            columns = List.of(8, 7, 6, 5, 4, 3, 2, 1);
        }

        printColNames(columns);
        for (int row : rows) {
            printRowNumber(row);
            for (int column : columns) {
                printSquare(board, row, column);
            }
            printRowNumber(row);
            reset();
            println("");
        }
        printColNames(columns);
    }

    public void printBoardHighlighted(ChessBoard board, ChessGame.TeamColor teamColor, ChessPosition position, Collection<ChessMove> moves) {
        List<Integer> rows;
        List<Integer> columns;
        if (teamColor == ChessGame.TeamColor.WHITE) {
            rows = List.of(8, 7, 6, 5, 4, 3, 2, 1);
            columns = List.of(1, 2, 3, 4, 5, 6, 7, 8);
        } else {
            rows = List.of(1, 2, 3, 4, 5, 6, 7, 8);
            columns = List.of(8, 7, 6, 5, 4, 3, 2, 1);
        }

        printColNames(columns);
        for (int row : rows) {
            printRowNumber(row);
            for (int column : columns) {
                if (position != null && position.getRow() == row && position.getColumn() == column) {
                    printSquareColored(board, row, column, BGColor.YELLOW, Color.BLACK);
                } else if (moves != null &&
                            moves.stream().anyMatch(
                                    move -> move.getEndPosition().getRow() == row &&
                                    move.getEndPosition().getColumn() == column
                            )) {
                    printHighlightedSquare(board, row, column);
                } else {
                    printSquare(board, row, column);
                }
            }
            printRowNumber(row);
            reset();
            println("");
        }
        printColNames(columns);
    }

    private void printRowNumber(int row) {
        reset();
        setBackground(BGColor.LIGHT_GREY);
        setColor(Color.BLACK);
        print(" " + row + " ");
    }

    private void printColNames(List<Integer> columns) {
        setBackground(BGColor.LIGHT_GREY);
        setColor(Color.BLACK);
        print("   ");
        for (int column : columns) {
            print(" " + (char)('a' + column - 1) + " ");
        }
        print("   ");
        reset();
        println("");
    }

    private void printSquare(ChessBoard board, int row, int column) {
        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = board.getPiece(position);

        BGColor bgColor;
        if ((row + column) % 2 == 0) {
            bgColor = BGColor.DARK_GREY;
        } else {
            bgColor = BGColor.WHITE;
        }

        Color color;

        if (piece != null && piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            color = Color.BLUE;
        } else {
            color = Color.RED;
        }

        printSquareColored(board, row, column, bgColor, color);
    }

    private void printHighlightedSquare(ChessBoard board, int row, int column) {
        if ((row + column) % 2 == 0) {
            printSquareColored(board, row, column, BGColor.DARK_GREEN, Color.BLACK);
        } else {
            printSquareColored(board, row, column, BGColor.GREEN, Color.BLACK);
        }
    }

    private void printSquareColored(ChessBoard board, int row, int column, BGColor bgColor, Color color) {
        setBackground(bgColor);
        setColor(color);

        ChessPosition position = new ChessPosition(row, column);
        ChessPiece piece = board.getPiece(position);
        String character = getCharacter(piece);

        this.print(" " + character + " ");
    }

    private static String getCharacter(ChessPiece piece) {
        String character = " ";
        if (piece == null) {
            character = " ";
        } else {
            switch (piece.getPieceType()) {
                case PAWN -> character = "P";
                case ROOK -> character = "R";
                case KNIGHT -> character = "N";
                case BISHOP -> character = "B";
                case QUEEN -> character = "Q";
                case KING -> character = "K";
            }
        }
        return character;
    }

}
