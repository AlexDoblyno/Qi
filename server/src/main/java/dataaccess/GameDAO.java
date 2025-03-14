package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public interface GameDAO {
    void clear() throws DataAccessException;

    int createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ArrayList<GameData> listGame() throws DataAccessException;

    void updateGame(GameData data) throws DataAccessException;
}
