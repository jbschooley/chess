package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;
    GameData createGame(String name) throws DataAccessException;
    GameData getGame(int id) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData updateGame() throws DataAccessException; // TODO args
}
