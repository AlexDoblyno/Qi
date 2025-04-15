package ui.uihandlers;

import serverfacade.ServerFacade;
import ui.Printer;

public class Handler {
    protected final Printer p = new Printer();

    protected final ServerFacade serverFacade;

    public Handler(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }
}
