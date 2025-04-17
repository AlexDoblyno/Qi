package ui;

import model.AuthToken;
import model.Game;
import resultclasses.*;
import serverfacade.ServerFacade;
import ui.uihandlers.*;

import java.util.List;
import java.util.Scanner;

public class CommandLineUI {

    private enum State {
        PRE_LOGIN,
        LOGGED_IN,
        GAMEPLAY
    }

    private final Printer p = new Printer();
    private final ServerFacade serverFacade = new ServerFacade("http://localhost:8080");

    private final HelpHandler helpHandler = new HelpHandler(serverFacade);
    private final LoginHandler loginHandler = new LoginHandler(serverFacade);
    private final RegisterHandler registerHandler = new RegisterHandler(serverFacade);
    private final LogoutHandler logoutHandler = new LogoutHandler(serverFacade);
    private final CreateHandler createHandler = new CreateHandler(serverFacade);
    private final JoinHandler joinHandler = new JoinHandler(serverFacade);
    private final ListHandler listHandler = new ListHandler(serverFacade);
    private final SpectateHandler spectateHandler = new SpectateHandler(serverFacade);

    private AuthToken authToken;
    private String username;
    private List<Game> gameList;
    private State state;

    public boolean replLoop(String input) {
        var args = input.split(" ");
        if (args.length == 0) {
            return true;
        }

        switch (args[0]) {
            case "help", "h" -> helpHandler.help(this.state == State.LOGGED_IN);
            case "quit", "q" -> {
                return false;
            }
            case "login", "log" -> checkStateAndLogin(args);
            case "register", "reg" -> checkStateAndRegister(args);
            case "logout", "exit" -> checkStateAndLogout();
            case "list", "ll", "show" -> checkStateAndList();
            case "join" -> checkStateAndJoin(args);
            case "create", "new" -> checkStateAndCreateGame(args);
            case "spectate", "observe" -> checkStateAndSpectate(args);
            default -> {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("Unknown command: " + input);
                helpHandler.help(this.state == State.LOGGED_IN);
            }
        }

        return true;
    }

    private void printUser() {
        p.reset();
        p.setColor(Printer.Color.GREEN);
        p.print("[" + username + "] >>> ");
        p.setColor(Printer.Color.YELLOW);
        p.print("");
    }

    private void checkStateAndSpectate(String[] args) {
        if (this.state == State.LOGGED_IN) {
            if (this.gameList == null) {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("You must list games first!");
            } else {
                JoinGameResult result = spectateHandler.spectate(args, this.authToken, this.gameList);
                if (result != null) {
                    this.state = State.GAMEPLAY;
                }
                boolean inGame = true;
                while (inGame) {
                    Scanner scanner = new Scanner(System.in);
                    printUser();
                    String input = scanner.nextLine();
                }
                this.state = State.LOGGED_IN;
            }
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are not logged in!");
        }
    }

    private void checkStateAndCreateGame(String[] args) {
        if (this.state == State.LOGGED_IN) {
            CreateGameResult result = createHandler.create(args, this.authToken);
            if (result == null) {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("Error creating game");
            }
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are not logged in!");
        }
    }

    private void checkStateAndJoin(String[] args) {
        if (this.state == State.LOGGED_IN) {
            if (this.gameList == null) {
                p.reset();
                p.setColor(Printer.Color.RED);
                p.println("You must list games first!");
            } else
            {
                JoinGameResult result = joinHandler.join(args, this.authToken, this.gameList);
                if (result != null) {
                    this.state = State.GAMEPLAY;
                    boolean inGame = true;
                    while (inGame) {
                        Scanner scanner = new Scanner(System.in);
                        printUser();
                        String input = scanner.nextLine();
                    }
                    this.state = State.LOGGED_IN;
                }
            }
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are not logged in!");
        }
    }

    private void checkStateAndList() {
        if (this.state == State.LOGGED_IN) {
            this.gameList = List.of(listHandler.list(this.authToken));
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are not logged in!");
        }
    }

    private void checkStateAndLogout() {
        if (this.state == State.LOGGED_IN) {
            logoutHandler.logout(this.authToken);
            this.state = State.PRE_LOGIN;
            p.println("Logged out!");
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are not logged in!");
        }
    }

    private void checkStateAndRegister(String[] args) {
        if (this.state == State.PRE_LOGIN) {
            this.authToken = registerHandler.register(args);
            if (this.authToken != null) {
                this.state = State.LOGGED_IN;
                this.username = args[1];
            }
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are already logged in!");
        }
    }

    private void checkStateAndLogin(String[] args) {
        if (this.state == State.PRE_LOGIN) {
            this.authToken = loginHandler.login(args);
            if (this.authToken != null) {
                this.state = State.LOGGED_IN;
                this.username = args[1];
            }
        } else {
            p.reset();
            p.setColor(Printer.Color.RED);
            p.println("You are already logged in!");
        }
    }

    public CommandLineUI() {
        this.state = State.PRE_LOGIN;
    }

    public void run() {
        initMessage();
        Scanner scanner = new Scanner(System.in);
        boolean shouldContinue = true;
        while (shouldContinue) {
            p.reset();
            p.setColor(Printer.Color.GREEN);
            if (this.state == State.PRE_LOGIN) {
                p.print("[240Chess] >>> ");
            } else {
                p.print("[" + username + "] >>> ");
            }
            p.setColor(Printer.Color.YELLOW);
            p.print("");
            String input = scanner.nextLine();
            shouldContinue = this.replLoop(input);
        }
    }

    public static void main(String[] args) {
        var self = new CommandLineUI();
        self.run();
    }

    private void initMessage() {
        p.setThickness(Printer.Thickness.BOLD);
        p.setColor(Printer.Color.YELLOW);
        p.println("Welcome to 240 Chess!");
        p.println("Type \"help\" for help");
    }
}
