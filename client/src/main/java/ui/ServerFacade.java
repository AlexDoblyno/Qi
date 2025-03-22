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