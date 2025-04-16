package ui.GameplayHandlers;

import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import client.WSFacade;

public class GSpectateHandler extends OnlineGameHandler {

    public GSpectateHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void joinGame(Integer gameId) throws ResponseException {
        wsFacade.sendJoinSpectator(authToken, gameId);
    }
}
