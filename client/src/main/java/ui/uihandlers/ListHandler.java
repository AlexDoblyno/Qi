package ui.uihandlers;

import model.AuthToken;
import model.Game;
import resultclasses.ListGamesResult;
import serverfacade.ServerFacade;
import ui.Printer;

public class ListHandler extends Handler {

    public ListHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public Game[] list(AuthToken authToken) {
        try {
            ListGamesResult res = serverFacade.listGames(authToken);
            p.reset();
            p.setColor(Printer.Color.GREEN);
            p.println("Games:");
            int index = 1;
            for (Game game : res.getGames()) {
                p.println(index + ") " + "Game ID: " + game.getGameID() + " | Game Name: " + game.getGameName() + " | White Username: " + game.getWhiteUsername() + " | Black Username: " + game.getBlackUsername());
                index++;
            }
            return res.getGames();
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }

    }
}
