package ui;

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

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help      - Displays this help message.");
        System.out.println("quit      - Exits the program.");
        System.out.println("login     - Logs in an existing user.");
        System.out.println("register  - Registers a new user account and logs in.");
    }