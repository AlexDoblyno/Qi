package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Map;

public class DrawBoard {
    private final String[][] chessBoard = new String[10][10];

    private final String borderSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK;

    public DrawBoard() {
        resetBoard();
        ChessGame chessGame = new ChessGame();
        placePieces(chessGame);
    }

    public void printBothBoards() {
        String[][] reverseBoard = reverseBoard();
        printBoard(reverseBoard);
        printBoard(chessBoard);
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }

    private void printBoard(String[][] boardToPrint) {
        System.out.println();

        for (String[] row : boardToPrint) {
            for (String cell : row) {
                System.out.print(cell + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
    }

    private void resetBoard() {
        String[] colLabels = {EscapeSequences.EMPTY, " A ", " B ", " C ", " D ", " E ", " F ", " G ", " H ", EscapeSequences.EMPTY};
        String[] rowLabels = {EscapeSequences.EMPTY, " 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 ", EscapeSequences.EMPTY};

        for (int row = 0; row < chessBoard.length; row++) {
            for (int col = 0; col < chessBoard[row].length; col++) {
                if (row == 0 || row == chessBoard.length - 1) {
                    chessBoard[row][col] = borderSquare + colLabels[col];
                }
                else if (col == 0 || col == chessBoard[0].length - 1) {
                    chessBoard[row][col] = borderSquare + rowLabels[row];
                }
                else if ((row + col) % 2 == 0) {
                    chessBoard[row][col] = EscapeSequences.SET_BG_COLOR_WHITE + EscapeSequences.EMPTY;
                }
                else {
                    chessBoard[row][col] = EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY;
                }
            }
        }
    }

    private void placePieces(ChessGame game) {
        ChessBoard board = game.getBoard();
        Map<ChessPosition, ChessPiece> pieceMap = new java.util.HashMap<>(Map.of());

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition currPosition = new ChessPosition(row + 1, col + 1);
                ChessPiece currPiece = board.getPiece(currPosition);
                if (currPiece != null) {
                    pieceMap.put(currPosition, currPiece);
                }
            }
        }

        for (Map.Entry<ChessPosition, ChessPiece> entry : pieceMap.entrySet()) {
            ChessPiece piece = entry.getValue();
            ChessPosition currPosition = entry.getKey();
            ChessPiece.PieceType pieceType = piece.getPieceType();
            ChessGame.TeamColor teamColor = piece.getTeamColor();
            int row = currPosition.getRow();
            int col = currPosition.getColumn();

            String pieceString = getPieceString(teamColor, pieceType);

            String backgroundColor = "";
            if ((row + col) % 2 == 0) {
                backgroundColor = EscapeSequences.SET_BG_COLOR_WHITE;
            }
            else {
                backgroundColor = EscapeSequences.SET_BG_COLOR_BLACK;
            }

            if (teamColor == ChessGame.TeamColor.BLACK) {
                chessBoard[row][col] =  backgroundColor + EscapeSequences.SET_TEXT_COLOR_BLUE + pieceString;
            }
            else {
                chessBoard[row][col] = backgroundColor + EscapeSequences.SET_TEXT_COLOR_RED + pieceString;
            }
        }
    }

    private String getPieceString(ChessGame.TeamColor teamColor, ChessPiece.PieceType pieceType) {
        String pieceString = "";
        switch (pieceType) {
            case KING -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_KING;
                } else {
                    pieceString = EscapeSequences.WHITE_KING;
                }
            }
            case QUEEN -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_QUEEN;
                } else {
                    pieceString = EscapeSequences.WHITE_QUEEN;
                }
            }
            case ROOK -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_ROOK;
                } else {
                    pieceString = EscapeSequences.WHITE_ROOK;
                }
            }
            case BISHOP -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_BISHOP;
                } else {
                    pieceString = EscapeSequences.WHITE_BISHOP;
                }
            }
            case KNIGHT -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_KNIGHT;
                } else {
                    pieceString = EscapeSequences.WHITE_KNIGHT;
                }
            }
            case PAWN -> {
                if (teamColor == ChessGame.TeamColor.BLACK) {
                    pieceString = EscapeSequences.BLACK_PAWN;
                } else {
                    pieceString = EscapeSequences.WHITE_PAWN;
                }
            }
        }
        return pieceString;
    }

    private String[][] reverseBoard() {
        int size = chessBoard.length;
        String[][] reversedBoard = new String[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                reversedBoard[row][col] = chessBoard[size - 1 - row][size - 1 - col];
            }
        }
        return reversedBoard;
    }
}