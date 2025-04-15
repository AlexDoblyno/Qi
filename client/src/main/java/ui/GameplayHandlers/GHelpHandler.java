package ui.GameplayHandlers;

import ui.Printer;

public class GHelpHandler extends GameHandler {

    public void help() {
        p.setColor(Printer.Color.YELLOW);
        p.println("Commands:");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("move <x1><y1><x2><y2> [promotion]");
        p.reset();
        p.println(" - Move a piece, if pawn include a promotion, eg. \"move a2a3\" or \"move a7a8 q\"");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("view <x><y>");
        p.reset();
        p.println(" - Highlight the possible moves for a particular square, eg. \"view a2\"");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("leave");
        p.reset();
        p.println(" - Leave the game and return to main menu (without resigning)");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("resign");
        p.reset();
        p.println(" - Forfeit the game");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("redraw");
        p.reset();
        p.println(" - Manually force a redraw of the game board");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("help");
        p.reset();
        p.println(" - Display this help message");
    }
}
