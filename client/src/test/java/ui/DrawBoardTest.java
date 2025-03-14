package ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.DrawBoard;

class DrawBoardTest {
    @Test
    void drawBoard() {
        DrawBoard drawBoard = new DrawBoard();
        Assertions.assertNotNull(drawBoard);
        drawBoard.printBothBoards();
    }
}