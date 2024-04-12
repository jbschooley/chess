package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear() throws DataAccessException;
    GameData createGame(String name) throws DataAccessException;
    GameData getGame(int id) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData updateGameUsername(int id, ChessGame.TeamColor color, String username) throws DataAccessException;
    GameData updateGameData(int id, ChessGame game) throws DataAccessException;
}
