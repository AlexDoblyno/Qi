package model;

public record GameData(Integer gameID, String whiteUsername, String blackUsername, String gameName,
                       chess.ChessGame game) {
}
