package ui.uihandlers;

import model.AuthToken;
import serverfacade.ServerFacade;
import ui.Printer;

public class LogoutHandler extends Handler {

    public LogoutHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public void logout(AuthToken authToken) {
        try {
            serverFacade.logout(authToken);
        } catch (Exception e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
        }
    }
}
