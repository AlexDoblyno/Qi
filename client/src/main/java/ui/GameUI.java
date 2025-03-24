package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

public class GameUI {

    private final Client client; // WebSocket client
    private final Scanner scanner;
    private final Integer gameID; // Game ID the user is connected to
    private final String authToken;
    private final ChessGame.TeamColor color;
    private WebSocketClient webSocketClient;
    private ChessGame chessGame;
    private static boolean closed = false;

    public GameUI(Client client, Integer gameID, ChessGame.TeamColor teamColor, String authToken) throws Exception {
        this.client = client;
        this.authToken = authToken;
        this.gameID = gameID;
        this.scanner = new Scanner(System.in);
        this.color = teamColor;
        try {
            this.webSocketClient = client.getServerFacade().createWebSocketClient(this);
        } catch (Exception e) {
            throw new Exception("Error creating web socket client");
        }
        try {
            UserGameCommand joinCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            webSocketClient.sendMessage(joinCommand);
        } catch (Exception e) {
            throw new Exception("Error joining web socket server");
        }
        if (teamColor == null) {
            webSocketClient.teamColor = ChessGame.TeamColor.WHITE;
        } else {
            webSocketClient.teamColor = teamColor;
        }
    }

    public void start() {
        System.out.println("Welcome to the game! Type 'help' for a list of commands.");

        while (true) {
            if (closed) {
                return;
            }
            System.out.print("[IN_GAME] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "redraw":
                    ChessGame.TeamColor bottom = (color == null) ? ChessGame.TeamColor.WHITE : color;
                    BoardPrinter.printBoard(chessGame.getBoard(), bottom, null);
                    break;
                case "leave":
                    if (leaveGame()) {
                        return;
                    }
                    break; // Exit the game UI
                case "move":
                    if (color == null) {
                        System.out.println("Invalid command for observer");
                        break;
                    }
                    makeMove();
                    break;
                case "resign":
                    if (color == null) {
                        System.out.println("Invalid command for observer");
                        break;
                    }
                    resignGame();
                    break;
                case "highlight":
                    highlightLegalMoves();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help       - Displays this help message.");
        System.out.println("redraw     - Redraws the chess board.");
        System.out.println("leave      - Leaves the game and returns to the main menu.");
        if (color != null) {
            System.out.println("move       - Makes a move in the game.");
            System.out.println("resign     - Resigns from the game.");
        }
        System.out.println("highlight  - Highlights legal moves for a selected piece.");
    }

    private Boolean leaveGame() {
        System.out.print("Are you sure you want to leave? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            webSocketClient.sendMessage(resignCommand);
            System.out.println("You have left the game.");
            return true;
        } else {
            System.out.println("Leave canceled.");
            return false;
        }
    }

    private void makeMove() {
        if (this.chessGame.isOver()) {
            System.out.println("The game is over.");
            return;
        }
        if (!this.chessGame.getTeamTurn().equals(color)) {
            System.out.println("It is not your turn.");
            return;
        }
        System.out.print("Enter the start position (e.g., e2): ");
        ChessPosition start = parsePosition(scanner.nextLine().trim());
        if (start == null) {
            return;
        }
        ChessPiece piece = chessGame.getBoard().getPiece(start);
        if (piece == null || piece.getTeamColor() != this.color) {
            System.out.println("Invalid piece");
            return;
        }
        System.out.print("Enter the end position (e.g., e4): ");
        ChessPosition end = parsePosition(scanner.nextLine().trim());
        if (end == null) {
            return;
        }

        // Check if it's a pawn upgrade move
        ChessPiece.PieceType promotion = null;
        assert piece != null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (((color == ChessGame.TeamColor.BLACK) && (end.getRow() == 1))
                    || ((color == ChessGame.TeamColor.WHITE) && (end.getRow() == 8))) {
                System.out.print("Enter promotion piece (e.g., queen): ");
                switch (scanner.nextLine().trim()) {
                    case "queen":
                        promotion = ChessPiece.PieceType.QUEEN;
                        break;
                    case "rook":
                        promotion = ChessPiece.PieceType.ROOK;
                        break;
                    case "bishop":
                        promotion = ChessPiece.PieceType.BISHOP;
                        break;
                    case "knight":
                        promotion = ChessPiece.PieceType.KNIGHT;
                        break;
                    default:
                        System.out.println("Invalid promotion piece");
                        makeMove();
                }
            }
        }

        ChessMove move = new ChessMove(start, end, promotion);
        try {
            chessGame.makeMove(move);
        } catch (InvalidMoveException e) {
            System.out.println("Invalid move");
            return;
        }

        try {
            UserGameCommand moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken,
                    gameID, move);

            webSocketClient.sendMessage(moveCommand);
        } catch (IllegalArgumentException e) {
            System.out.println("Error making move: " + e.getMessage());
        }
    }

    private void resignGame() {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            webSocketClient.sendMessage(resignCommand);
            System.out.println("You have resigned the game.");
        } else {
            System.out.println("Resignation canceled.");
        }
    }


