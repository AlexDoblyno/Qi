package ui.uihandlers;

import serverfacade.ServerFacade;
import ui.Printer;

public class HelpHandler extends Handler {

    public HelpHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public void help(boolean loggedIn) {
        p.setColor(Printer.Color.YELLOW);
        p.println("Commands:");
        if (loggedIn) {
            helpPostLogin();
        } else {
            helpPreLogin();
        }
        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("quit");
        p.reset();
        p.println(" - Quit the program");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("help");
        p.reset();
        p.println(" - Display this help message");
    }

    public void helpPostLogin() {
        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("create <name>");
        p.reset();
        p.println(" - Create a new game");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("list");
        p.reset();
        p.println(" - List all games");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("join <ID> [WHITE|BLACK|RANDOM]");
        p.reset();
        p.println(" - Join a game");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("observe <ID>");
        p.reset();
        p.println(" - Observe a game");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("logout");
        p.reset();
        p.println(" - Log out of your account");
    }

    public void helpPreLogin() {
        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("login <username> <password>");
        p.reset();
        p.println(" - Log in to your account");

        p.setColor(Printer.Color.YELLOW);
        p.setIndent(4);
        p.print("register <username> <password> <email>");
        p.reset();
        p.println(" - Create a new account");
    }
}
