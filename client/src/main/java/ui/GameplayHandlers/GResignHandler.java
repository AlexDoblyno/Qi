package ui.GameplayHandlers;

import exceptions.ResponseException;
import model.AuthToken;
import wsfacade.WSFacade;

public class GResignHandler extends OnlineGameHandler {

    public GResignHandler(WSFacade wsFacade, AuthToken authToken) {
        super(wsFacade, authToken);
    }

    public void resign(Integer gameID) throws ResponseException {
        wsFacade.sendResign(authToken, gameID);
    }
}
