package ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Scanner;

public class PreloginUI {

    private final Client client;
    private final Scanner scanner;
    private String authToken;

    public PreloginUI(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
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