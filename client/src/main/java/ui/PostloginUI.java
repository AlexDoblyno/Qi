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