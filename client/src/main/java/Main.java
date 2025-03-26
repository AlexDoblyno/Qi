import chess.*;
import ui.Client;
import ui.PreloginUI;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        Client client = new Client("8080");
        PreloginUI preloginUI = new PreloginUI(client);
        preloginUI.start();
    }
}