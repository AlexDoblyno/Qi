package websocketmessages.servermessages;

import chess.ChessGame;
import com.google.gson.GsonBuilder;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        var GSON = new GsonBuilder().registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameJson()).create();
        this.game = GSON.fromJson(game, ChessGame.class);
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LoadGameMessage that = (LoadGameMessage) o;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), game);
    }
}
