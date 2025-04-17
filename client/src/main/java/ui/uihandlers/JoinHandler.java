package ui.uihandlers;

import chess.ChessGame;
import model.AuthToken;
import model.Game;
import resultclasses.JoinGameResult;
import serverfacade.ServerFacade;
import ui.Printer;

import java.util.List;

public class JoinHandler extends Handler {

    public JoinHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public JoinGameResult join(String[] args, AuthToken authToken, List<Game> previousList) {
        if (args.length < 2) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: Invalid number of arguments");
            p.println("Usage: join <gameIndex> [playerColor]");
            return null;
        }
        try {
            int gameIndex = Integer.parseInt(args[1]);
            if (gameIndex < 1 || gameIndex > previousList.size()) {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("Error: Invalid game index");
                return null;
            }
            Game selectedGame = previousList.get(gameIndex - 1);
            int gameID = selectedGame.getGameID();
            if (args.length > 2) {
                String playerColor = args[2];
                if (!playerColor.equals("BLACK") && !playerColor.equals("WHITE")) {
                    p.reset();
                    p.setColor(Printer.Color.RED);
                    p.println("Error: Invalid player color, must be BLACK or WHITE or left blank");
                    return null;
                }
                var result = serverFacade.joinGame(authToken, Integer.valueOf(gameID), playerColor);
                result.setPlayerColor(ChessGame.TeamColor.valueOf(playerColor));
                result.setGameID(gameID);
                return result;
            } else {
                boolean whiteAvailable = selectedGame.getWhiteUsername() == null;
                boolean blackAvailable = selectedGame.getBlackUsername() == null;
                if (whiteAvailable) {
                    p.reset();
                    p.setColor(Printer.Color.BLUE);
                    p.println("Joining as white");
                    var result = serverFacade.joinGame(authToken, Integer.valueOf(gameID), "WHITE");
                    result.setPlayerColor(ChessGame.TeamColor.WHITE);
                    result.setGameID(gameID);
                    return result;
                } else if (blackAvailable) {
                    p.reset();
                    p.setColor(Printer.Color.RED);
                    p.println("Joining as black");

                    var result = serverFacade.joinGame(authToken, Integer.valueOf(gameID), "BLACK");
                    result.setPlayerColor(ChessGame.TeamColor.BLACK);
                    result.setGameID(gameID);
                    return result;
                } else {
                    p.reset();
                    p.setColor(Printer.Color.RED);
                    p.println("Error: Both colors are already taken");
                    return null;
                }
            }
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }
    }
}