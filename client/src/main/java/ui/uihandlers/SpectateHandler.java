package ui.uihandlers;

import model.AuthToken;
import model.Game;
import resultclasses.JoinGameResult;
import serverfacade.ServerFacade;
import ui.Printer;

import java.util.List;

public class SpectateHandler extends Handler {

    public SpectateHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public JoinGameResult spectate(String[] words, AuthToken authToken, List<Game> previousList) {
        if (words.length != 2) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: Invalid number of arguments");
            p.println("Usage: spectate <gameIndex>");
            return null;
        }
        try {
            int gameIndex = Integer.parseInt(words[1]);
            if (gameIndex < 1 || gameIndex > previousList.size()) {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("Error: Invalid game index");
                return null;
            }
            Game selectedGame = previousList.get(gameIndex - 1);
            String gameID = selectedGame.getGameID();
            var result = serverFacade.joinGame(authToken, Integer.valueOf(gameID), null);
            result.setGameID(Integer.parseInt(gameID));
            return result;
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }
    }
}
