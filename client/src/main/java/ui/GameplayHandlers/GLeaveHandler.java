package ui.GameplayHandlers;

import exceptions.ResponseException;
import model.AuthToken;
import wsfacade.WSFacade;

public class GLeaveHandler extends OnlineGameHandler {

    public GLeaveHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void leave(Integer gameID) throws ResponseException {
        wsFacade.sendLeaveGame(authToken, gameID);
    }
}
