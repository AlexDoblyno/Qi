package ui.GameplayHandlers;

import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import client.WSFacade;

public class GResignHandler extends OnlineGameHandler {

    public GResignHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void resign(Integer gameID) throws ResponseException {
        wsFacade.sendResign(authToken, gameID);
    }
}
