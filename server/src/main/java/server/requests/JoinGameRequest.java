package server.requests;

import chess.ChessGame;

public record JoinGameRequest(Integer gameID, ChessGame.TeamColor playerColor) {
}
