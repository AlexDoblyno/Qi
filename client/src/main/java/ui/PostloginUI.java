package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

public class PostloginUI {

    private final Client client;
    private final Scanner scanner;
    private final Map<String, Integer> gameList; // Maps displayed game numbers to game IDs for easy access
    private final List<String> gameNames;
    private final String authToken;

    public PostloginUI(Client client, String token) {
        this.client = client;
        this.gameNames = new ArrayList<>();
        this.scanner = new Scanner(System.in);
        this.gameList = new HashMap<>();
        this.authToken = token;
    }

    // Main loop for handling user input
    public void start() {
        System.out.println("You are logged in! Type 'help' for a list of commands.");

        while (true) {
            try {
                updateGames();
            } catch (Exception e) {
                System.out.println("Error getting games from server: " + e.getMessage());
            }
            System.out.print("[LOGGED_IN] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    handleLogout();
                    return; // Exit to go back to PreloginUI
                case "create game":
                    handleCreateGame();
                    break;
                case "list games":
                    handleListGames();
                    break;
                case "play game":
                    handlePlayGame();
                    break;
                case "observe game":
                    handleObserveGame();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void updateGames() throws Exception {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(client.getServerFacade().listGames(authToken), JsonObject.class);

        JsonArray gamesArray = jsonObject.getAsJsonArray("games");

        // Reset game list map
        gameList.clear();
        gameNames.clear();

        for (int i = 0; i < gamesArray.size(); i++) {
            JsonObject game = gamesArray.get(i).getAsJsonObject();

            // Extract and print game properties
            int gameID = game.get("gameID").getAsInt();
            String gameName = game.get("gameName").getAsString();

            gameList.put(gameName, gameID);
            gameNames.add(gameName);
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help         - Displays this help message.");
        System.out.println("logout       - Logs out and returns to the login screen.");
        System.out.println("create game  - Creates a new game (does not join the player).");
        System.out.println("list games   - Lists all active games on the server.");
        System.out.println("play game    - Joins a game as a player.");
        System.out.println("observe game - Observes a game.");
    }

    private void handleLogout() {
        try {
            String response = client.getServerFacade().logout(authToken);
            if (response.isEmpty()) {
                System.out.println("Logout successful. Returning to login screen.");
                new PreloginUI(client).start();
            } else {
                System.out.println("Logout failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during logout: " + e.getMessage());
        }
    }

    private void handleCreateGame() {
        System.out.print("Enter a name for the new game: ");
        String gameName = scanner.nextLine();

        try {
            String response = client.getServerFacade().createGame(gameName, authToken);
            if (response.contains("gameID")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            } else {
                System.out.println("Failed to create game. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while creating the game: " + e.getMessage());
        }
        try {
            updateGames();
        } catch (Exception e) {
            System.out.println("Error getting games from server: " + e.getMessage());
        }
    }

    private void handleListGames() {
        try {
            String response = client.getServerFacade().listGames(authToken);
            if (response.contains("games")) {  // Assuming response structure contains games
                System.out.println("Available games:");

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                JsonArray gamesArray = jsonObject.getAsJsonArray("games");

                // Reset game list map
                gameList.clear();
                gameNames.clear();

                for (int i = 0; i < gamesArray.size(); i++) {
                    JsonObject game = gamesArray.get(i).getAsJsonObject();

                    // Extract and print game properties
                    int gameID = game.get("gameID").getAsInt();
                    String gameName = game.get("gameName").getAsString();

                    String blackUser;
                    String whiteUser;
                    if (game.toString().contains("blackUsername")) {
                        blackUser = game.get("blackUsername").getAsString();
                    } else {
                        blackUser = "none";
                    }
                    if (game.toString().contains("whiteUsername")) {
                        whiteUser = game.get("whiteUsername").getAsString();
                    } else {
                        whiteUser = "none";
                    }

                    gameList.put(gameName, gameID);
                    gameNames.add(gameName);

                    System.out.println("Game Number: " + (i+1) + ", Game Name: " + gameName + ", White Username: "
                            + whiteUser + ", Black Username: " + blackUser);
                }
            } else {
                System.out.println("No games currently available.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while listing games: " + e.getMessage());
        }
    }

    private void handlePlayGame() {
        System.out.print("Enter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());

        if (gameNames.size() < gameNumber || gameNumber < 1) {
            System.out.println("Invalid game number. Please list games and try again.");
            return;
        }

        System.out.print("Enter your color (e.g., 'white' or 'black'): ");
        ChessGame.TeamColor color = (Objects.equals(scanner.nextLine(), "white")) ? ChessGame.TeamColor.WHITE :
                ChessGame.TeamColor.BLACK;

        try {
            String response = client.getServerFacade().playGame(String.valueOf(gameNumber), color, authToken);
            if (response.isEmpty()) {  // Adjust based on actual server response
                System.out.println("Joined game successfully.");

                // Transition to GameUI for gameplay
                new GameUI(client, gameList.get(gameNames.get(gameNumber-1)), color, authToken).start();
            } else {
                System.out.println("Failed to join game. Please try another.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while joining the game: " + e.getMessage());
        }
    }


    private void handleObserveGame() {
        System.out.print("Enter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());

        if (gameNames.size() < gameNumber || gameNumber < 1) {
            System.out.println("Invalid game number. Please list games and try again.");
            return;
        }

        try {
            new GameUI(client, gameList.get(gameNames.get(gameNumber-1)), null, authToken).start();
        } catch (Exception e) {
            System.out.println("An error occurred while attempting to observe the game: " + e.getMessage());
        }
    }
}