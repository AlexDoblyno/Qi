package ui.GameplayHandlers;

import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import client.WSFacade;

public class GLeaveHandler extends OnlineGameHandler {

    public GLeaveHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void leave(Integer gameID) throws ResponseException {
        wsFacade.sendLeaveGame(authToken, gameID);
    }
}
