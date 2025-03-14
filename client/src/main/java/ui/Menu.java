package ui;

import chess.ChessGame;
import help.ServerFacade;
import model.AuthData;
import model.GameData;
import java.util.HashMap;
import java.util.Map;
import static java.lang.System.exit;
import static ui.EscapeSequences.*;

public class Menu {
    private enum UserState {
        LOGGED_OUT,
        LOGGED_IN,
        WHITE,
        BLACK,
        OBSERVER
    }

    private final ServerFacade server;
    private GameData gameData;
    private GameData[] gameList;
    private final Map<Integer, Integer> gameIdMap = new HashMap<>();
    Map<Integer, Integer> reverseMap = new HashMap<>();
    private final DrawBoard drawBoard = new DrawBoard();
    private String username;
    private String authToken;
    private UserState currentState = UserState.LOGGED_OUT;
    private final String failure = "Couldn't process your command: ";

    public Menu(String hostname, int portNumber) {
        server = new ServerFacade(hostname, portNumber);
    }

    public void printInputPrompt() {
        if (currentState.equals(UserState.LOGGED_OUT)) {
            System.out.print("[LOGGED_OUT] >> ");
        }
        else {
            System.out.print("[LOGGED_IN] >> ");
        }
    }

    public void handleInput(String input) throws Exception {
        String[] params = input.split(" ");
        String command = params[0].toLowerCase();
        switch (currentState) {
            case LOGGED_OUT -> {
                switch (command) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "help" -> printHelp();
                    case "quit" -> quit();
                    default -> System.out.println("Invalid command: " + command);
                }
            }
            case LOGGED_IN, WHITE, BLACK, OBSERVER -> {
                switch (command) {
                    case "logout" -> logout();
                    case "list" -> list();
                    case "create" -> createGame(params);
                    case "join" -> join(params);
                    case "observe" -> observe(params);
                    case "help" -> printHelp();
                    case "quit" -> quit();
                    default -> System.out.println("Invalid command: " + command);
                }
            }
        }
    }

    private void printHelp() {
        StringBuilder returnString = new StringBuilder();
        switch (currentState) {
            case LOGGED_OUT -> {
                returnString.append(SET_TEXT_COLOR_MAGENTA + "login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR)
                        .append(" - Login to an existing account." + "\n");
                returnString.append(SET_TEXT_COLOR_MAGENTA + "register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR)
                        .append(" - Register a new account." + "\n");
            }
            case LOGGED_IN, WHITE, BLACK, OBSERVER -> {
                returnString.append(SET_TEXT_COLOR_MAGENTA).append("logout").append(RESET_TEXT_COLOR)
                        .append(" - Log out of ").append(username).append(".").append("\n");
                returnString.append(SET_TEXT_COLOR_MAGENTA).append("create <Name>").append(RESET_TEXT_COLOR).append(" - Creates a new game.\n");
                returnString.append(SET_TEXT_COLOR_MAGENTA).append("list").append(RESET_TEXT_COLOR).append(" - Lists all active games.\n");
                returnString.append(SET_TEXT_COLOR_MAGENTA).append("join <ID> [WHITE|BLACK]").append(RESET_TEXT_COLOR)
                        .append(" - Joins a game with the given ID.\n");
            }
        }
        returnString.append(SET_TEXT_COLOR_MAGENTA + "help" + RESET_TEXT_COLOR + " - List possible operations." + "\n");
        returnString.append(SET_TEXT_COLOR_MAGENTA + "quit" + RESET_TEXT_COLOR + " - Quit the program." + "\n");
        System.out.print(returnString);
    }

    private void login(String[] params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESET_TEXT_COLOR + RESET_BG_COLOR);
        if (params.length < 3) {
            System.out.println(SET_TEXT_COLOR_RED + failure + "You dont have the right number of arguments. See Help.");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }

        try {
            username = params[1];
            String password = params[2];

            loginUser(username, password);

            stringBuilder.append(SET_TEXT_COLOR_GREEN + "Logged in as ").append(username);
            stringBuilder.append(RESET_TEXT_COLOR).append(".").append("\n");
        }
        catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + failure + "That username/password combination does not exist");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }
        System.out.print(stringBuilder);
    }

    private void register(String[] params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESET_TEXT_COLOR + RESET_BG_COLOR);

        if (params.length < 4) {
            System.out.println(SET_TEXT_COLOR_RED +failure + "You dont have the right number of arguments. See Help.");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }

        try{
            username = params[1];
            String password = params[2];
            String email = params[3];
            server.register(username, password, email);
            loginUser(username, password);
            stringBuilder.append(SET_TEXT_COLOR_GREEN + "Registered user ").append(username).append(".").append("\n");
            stringBuilder.append(RESET_TEXT_COLOR);
        }
        catch (Exception e) {
            handleError(e);
        }
        System.out.print(stringBuilder);
    }

    private void logout() throws Exception {
        server.logout(authToken);
        currentState = UserState.LOGGED_OUT;
        username = null;
        authToken = null;
        System.out.println("Logged out successfully.");
    }

    private void quit() throws Exception {
        if (currentState.equals(UserState.LOGGED_IN)) {
            logout();
        }
        System.out.println(SET_TEXT_COLOR_BLUE + "Goodbye! :)");
        exit(0);
    }

    private void list() throws Exception {
        updateGameList();
        StringBuilder stringBuilder = new StringBuilder();

        for (GameData gameData : gameList) {
            int gameId = gameIdMap.get(gameData.gameID());
            stringBuilder.append(SET_TEXT_UNDERLINE + "Game Name:" + RESET_TEXT_UNDERLINE + " ").append(gameData.gameName())
                    .append(" " + SET_TEXT_UNDERLINE + "Game ID:" + RESET_TEXT_UNDERLINE + " ").append(gameId)
                    .append(" " + SET_TEXT_UNDERLINE + "White Player:"+ RESET_TEXT_UNDERLINE + " ")
                    .append(gameData.whiteUsername() != null ? gameData.whiteUsername() : "Empty")
                    .append(" " + SET_TEXT_UNDERLINE + "Black Player:" + RESET_TEXT_UNDERLINE + " ")
                    .append(gameData.blackUsername() != null ? gameData.blackUsername() : "Empty")
                    .append("\n");
        }
        System.out.print(stringBuilder);
    }

    private void createGame(String[] params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESET_TEXT_COLOR + RESET_BG_COLOR);

        if (params.length < 2) {
            System.out.println(SET_TEXT_COLOR_RED +failure + "You dont have the right number of arguments. See Help.");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }

        try{
            StringBuilder nameBuilder = new StringBuilder();

            for (int i = 1; i < params.length; i++) {
                nameBuilder.append(params[i]).append(" ");
            }

            String gameName = nameBuilder.toString();
            gameData = server.createGame(gameName, authToken);
            updateGameList();
            int gameId = gameIdMap.get(gameData.gameID());
            stringBuilder.append(SET_TEXT_COLOR_GREEN + "Created game ").append(gameName);
            stringBuilder.append(RESET_TEXT_COLOR).append(" with ID ").append(gameId).append("\n");
        }
        catch (Exception e) {
            handleError(e);
        }
        System.out.print(stringBuilder);
    }

    private void join(String[] params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESET_TEXT_COLOR + RESET_BG_COLOR);
        if (params.length < 3) {
            System.out.println(SET_TEXT_COLOR_RED +failure + "You dont have the right number of arguments. See Help.");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }
        String teamColor = params[2];

        if (!teamColor.equals("WHITE") && !teamColor.equals("BLACK")) {
            System.out.println(SET_TEXT_COLOR_RED +failure + "Please either select WHITE or BLACK!");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }

        try{
            updateGameList();
            int joinGameID = Integer.parseInt(params[1]);
            ChessGame.TeamColor team = ChessGame.TeamColor.valueOf(teamColor);
            joinGameID = reverseMap.get(joinGameID);

            if (team == ChessGame.TeamColor.BLACK) {
                currentState = UserState.BLACK;
            }
            else {
                currentState = UserState.WHITE;
            }

            gameData = server.joinGame(joinGameID, team, authToken);
            drawBoard.printBothBoards();
            updateGameList();
            int gameId = gameIdMap.get(gameData.gameID());
            stringBuilder.append(SET_TEXT_COLOR_GREEN + "Joined game ").append(gameId)
                    .append(RESET_TEXT_COLOR + " as ").append(team).append("\n");
            System.out.print(stringBuilder);
        }
        catch (Exception e) {
            handleError(e);
        }
    }

    private void observe(String[] params) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(RESET_TEXT_COLOR + RESET_BG_COLOR);

        if (params.length < 2) {
            System.out.println(SET_TEXT_COLOR_RED +failure + "You dont have the right number of arguments. See Help.");
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            return;
        }
        int gameID = Integer.parseInt(params[1]);
        gameList = server.listGames(authToken);

        for (GameData game : gameList) {
            if (game.gameID() == gameID) {
                gameData = game;
                drawBoard.printBothBoards();
                currentState = UserState.OBSERVER;
            }
        }
    }

    private void updateGameList() throws Exception {
        gameList = server.listGames(authToken);

        for (int i = 0; i < gameList.length; i++) {
            gameIdMap.put(gameList[i].gameID(), (i + 1));
            reverseMap.put((i + 1), gameList[i].gameID());
        }
    }

    private void loginUser(String username, String password) throws Exception {
        AuthData loginData = server.login(username, password);
        authToken = loginData.authToken();
        currentState = UserState.LOGGED_IN;
    }

    private void handleError(Exception e) {
        String message = e.getMessage();
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append(SET_TEXT_COLOR_RED);

        if (message.contains("already taken")) {
            errorBuilder.append("ERROR! The selected option is already taken!");
        }
        else if (message.contains("unauthorized")) {
            errorBuilder.append("ERROR! You do not have permission to perform this action!");
        }
        else {
            errorBuilder.append("ERROR! Something went wrong! ").append(e.getMessage());
        }

        errorBuilder.append(RESET_TEXT_COLOR).append("\n");
        System.out.println(errorBuilder);
    }
}