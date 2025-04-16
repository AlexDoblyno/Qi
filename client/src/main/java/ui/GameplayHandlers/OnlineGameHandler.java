package ui.GameplayHandlers;

import model.AuthToken;
import client.WSFacade;

public class OnlineGameHandler extends GameHandler {
    protected final WSFacade wsFacade;
    protected final AuthToken authToken;

    public OnlineGameHandler(WSFacade wsFacade, AuthToken authToken) {
        this.wsFacade = wsFacade;
        this.authToken = authToken;
    }
}
