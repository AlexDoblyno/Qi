package ui.GameplayHandlers;

import exceptions.ResponseException;
import model.AuthToken;
import wsfacade.WSFacade;

public class GSpectateHandler extends OnlineGameHandler {

    public GSpectateHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void joinGame(Integer gameId) throws ResponseException {
        wsFacade.sendJoinSpectator(authToken, gameId);
    }
}
