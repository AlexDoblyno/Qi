package serverfacade;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.GameList;
import ui.GameplayUI;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

//This is the main interaction between program and client while in a game
public class GamingInteraction {
    boolean inGame = true;
    private WebSocketFacade ws;
    String authToken;
    int gameID;
    private final ServerFacade serverFacade;
    private final String serverUrl;
    private final ChessGame.TeamColor colorChoice;
    private final ChessGame chessGame;
    private final NotificationHandler notificationHandler;
    private GameData gameData;

    public GamingInteraction(String serverUrl, NotificationHandler notificationHandler, String authToken, int gameID, ChessGame.TeamColor colorChoice, WebSocketFacade ws, GameData gameData) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.serverUrl = serverUrl;
        serverFacade = new ServerFacade(serverUrl);
        this.colorChoice = colorChoice;
        chessGame = new ChessGame();
        this.notificationHandler = notificationHandler;
        this.ws = ws;
        this.gameData = gameData;

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (inGame) {
            //Take in the input and evaluate it
            String line = scanner.nextLine();
            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    //Evaluate the User Input
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redrawChessBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String redrawChessBoard() throws ResponseException {
        ws.redraw(gameData, colorChoice);
        return "";
    }

    public String leaveGame() throws IOException {
        ws.leaveGame(authToken, gameID);
        inGame = false;
        return "";
    }

    public String makeMove(String... params) throws Exception {
        //Translate the Position to an actual ChessPosition
        ChessPosition startPosition = translatePosition(params[0]);
        ChessPosition endPosition = translatePosition(params[1]);

        //Retrieving the Piece at this location
        AuthData auth = new AuthData(authToken, null);
        GameList games = serverFacade.listGames(auth);
        GameData thisGame = new GameData(0, null, null, null, null, false);
        for (var c : games.games()) {
            if (c.gameID() == gameID) {
                thisGame = c;
            }
        }

        //WHAT IF INSTEAD WE CALL VALIDMOVES IN CHESSGAME AND MATCH THE START AND END WITH THE MOVES IN THERE.
        // THOSE SHOULD STORE THE PROMO PIECE TOO
        Collection<ChessMove> moves = chessGame.validMoves(startPosition);
        ChessPiece.PieceType promoPiece = null;
        for (var move : moves) {
            if (move.getEndPosition().equals(endPosition)) {
                if (move.getPromotionPiece() != null) {
                    Scanner scanner = new Scanner(System.in);
                    String line = scanner.nextLine();
                    var tokens = line.toLowerCase().split(" ");
                    var cmd = (tokens.length > 0) ? tokens[0] : "help";
                    switch (cmd) {
                        case "ROOK" -> promoPiece = ChessPiece.PieceType.ROOK;
                        case "KNIGHT" -> promoPiece = ChessPiece.PieceType.KNIGHT;
                        case "BISHOP" -> promoPiece = ChessPiece.PieceType.BISHOP;
                        case "QUEEN" -> promoPiece = ChessPiece.PieceType.QUEEN;
                    }
                }
            }
        }

        //Create and Make the move
        ChessMove move = new ChessMove(startPosition, endPosition, promoPiece);
        ws.makeMove(authToken, gameID, move);
        return "";
    }

    public String resignGame() throws IOException {
        System.out.println("Are you sure you want to resign?");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        var tokens = line.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        if (cmd.equals("yes")) {
            ws.resignGame(authToken, gameID);
            inGame = false;
        }
        return "";
    }

    public String highlightMoves(String... params) throws Exception {
        //Create the ChessPosition from the input
        ChessPosition position = translatePosition(params[0]);
        ws.highlight(gameData, colorChoice, position);
        return "";
    }

    public ChessPosition translatePosition(String input) throws Exception {
        return new ChessPosition(input.charAt(1) - '0', parseChar(input.charAt(0)));
    }

    int parseChar(char in) throws Exception {
        in = Character.toLowerCase(in);
        switch (in) {
            case 'a' -> {
                return 1;
            }
            case 'b' -> {
                return 2;
            }
            case 'c' -> {
                return 3;
            }
            case 'd' -> {
                return 4;
            }
            case 'e' -> {
                return 5;
            }
            case 'f' -> {
                return 6;
            }
            case 'g' -> {
                return 7;
            }
            case 'h' -> {
                return 8;
            }
            default -> throw new Exception("really bad");
        }
    }

    public String help() throws ResponseException {
        return """
                - Redraw the Chess Board: redraw
                - Make a Move: move <Start Position> <End Position>
                - Highlight the Legal Moves: highlight <Starting Position>
                - Leave the Game: leave
                - Resign the Game: resign
                """;
    }
}