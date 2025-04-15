package ui.uihandlers;

import model.AuthToken;
import resultclasses.CreateGameResult;
import serverfacade.ServerFacade;
import ui.Printer;

public class CreateHandler extends Handler {

    public CreateHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public CreateGameResult create(String[] args, AuthToken authToken) {
        if (args.length != 2) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: Invalid number of arguments");
            p.println("Usage: create <gameName>");
            return null;
        }
        try {
            var res = serverFacade.createGame(authToken, args[1]);
            p.reset();
            p.setColor(Printer.Color.GREEN);
            p.println("Game created!");
            return res;
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }
    }
}
