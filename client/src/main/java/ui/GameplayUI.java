package ui;

import chess.ChessGame;
import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import serverfacade.ServerFacade;
import ui.GameplayHandlers.*;
import client.WSFacade;

public class GameplayUI extends GameHandler {

    private final AuthToken authToken;
    private final Integer gameID;
    private final ServerFacade serverFacade;

    private final WSFacade wsFacade;

    private final GHelpHandler helpHandler;
    private final GHighlightHelper highlightHandler;
    private final GMoveHandler moveHandler;
    private final GJoinHandler joinHandler;
    private final GSpectateHandler joinSpectateHandler;
    private final GLeaveHandler leaveHandler;
    private final GResignHandler resignHandler;
    private final GRedrawHandler redrawHandler;

    public GameplayUI(AuthToken authToken, String URL, ChessGame.TeamColor teamColor, Integer gameID) throws ResponseException {
        super();
        this.authToken = authToken;
        this.setTeamColor(teamColor);
        this.gameID = gameID;
        this.serverFacade = new ServerFacade(URL);
        this.wsFacade = new WSFacade(URL, this);

        // Set up the local handlers
        this.helpHandler = new GHelpHandler();
        this.highlightHandler = new GHighlightHelper(teamColor);
        this.redrawHandler = new GRedrawHandler(teamColor);

        // Set up the online handlers
        this.moveHandler = new GMoveHandler(wsFacade, authToken);
        this.joinHandler = new GJoinHandler(wsFacade, authToken);
        this.joinSpectateHandler = new GSpectateHandler(wsFacade, authToken);
        this.leaveHandler = new GLeaveHandler(wsFacade, authToken);
        this.resignHandler = new GResignHandler(wsFacade, authToken);
    }

    public void join() throws ResponseException {
        joinHandler.joinGame(this.gameID, this.getTeamColor());
    }

    public boolean replLoop(String input) {
        try {
            var args = input.split(" ");
            if (args.length == 0) {
                return true;
            }

            switch (args[0]) {
                case "help", "h" -> helpHandler.help();
                case "quit", "q" -> {
                    return false;
                }
                case "move" -> moveHandler.extractMoveAndSend(gameID, args);
                case "view" -> highlightHandler.extractPositionAndView(game, args);

                case "leave" -> {
                    leaveHandler.leave(gameID);
                    return false;
                }
                case "resign" -> resignHandler.resign(gameID);
                case "redraw" -> redrawHandler.redraw(game);
                default -> {
                    p.reset();
                    p.setColor(Printer.Color.RED);
                    p.println("Unknown command: " + input);
                    helpHandler.help();
                }
            }
            return true;
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Unexpected Error: " + e.getMessage());
            return true;
        }
    }

    public void spectate() throws ResponseException {
        joinSpectateHandler.joinGame(this.gameID);
    }
}
