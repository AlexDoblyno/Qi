package ui.uihandlers;

import ResponseExceptionHandler.ResponseException;
import resultclasses.LoginResult;
import serverfacade.ServerFacade;
import ui.Printer;

public class LoginHandler extends Handler {

    public LoginHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public model.AuthToken login(String[] args) {
        if (args.length != 3) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: Invalid number of arguments");
            p.println("Usage: login <username> <password>");
            return null;
        }

        try {
            LoginResult res = serverFacade.login(args[1], args[2]);
            if (res.getStatus() != 200) throw new ResponseException(res.getStatus(), res.getMessage());
            p.reset();
            p.setColor(Printer.Color.GREEN);
            p.println("Logged in!");
            return res.getAuthToken();
        } catch (ResponseException e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }
    }
}
