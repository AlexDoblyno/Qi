package ui.GameplayHandlers;

import chess.ChessGame;
import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import client.WSFacade;

public class GJoinHandler extends OnlineGameHandler {

    public GJoinHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void joinGame(Integer gameId, ChessGame.TeamColor teamColor) throws ResponseException {
        wsFacade.sendJoinPlayer(authToken, gameId, teamColor);
    }
}
