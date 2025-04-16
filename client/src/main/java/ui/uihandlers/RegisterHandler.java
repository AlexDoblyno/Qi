package ui.uihandlers;

import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import resultclasses.RegisterResult;
import serverfacade.ServerFacade;
import ui.Printer;

public class RegisterHandler extends Handler {

    public RegisterHandler(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public AuthToken register(String[] args) {
        if (args.length != 4) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: Invalid number of arguments");
            p.println("Usage: register <username> <password> <email>");
            return null;
        }

        try {
            RegisterResult res = serverFacade.register(args[1], args[2], args[3]);
            p.reset();
            p.setColor(Printer.Color.GREEN);
            p.println("Registered!");
            return res.getAuthToken();
        } catch (ResponseException e) {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("Error: " + e.getMessage());
            return null;
        }
    }
}
