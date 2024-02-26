package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {

    SortedMap<Integer, GameData> games = new TreeMap<Integer, GameData>();

    public void clear() throws DataAccessException {
        games.clear();
    }

    public GameData createGame(String name) throws DataAccessException {
        int thisID;
        try {
            int lastID = games.lastKey();
            thisID = lastID + 1;
        } catch (NoSuchElementException e) {
            thisID = 1;
        }
        ChessGame cg = new ChessGame();
        GameData g = new GameData(thisID, null, null, name, cg);
        games.put(thisID, g);
        return g;
    }

    public GameData getGame(int id) throws DataAccessException {
        return games.get(id);
    }

    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = new HashSet<>();
        for (Map.Entry<Integer, GameData> e : games.entrySet()) {
            gameList.add(e.getValue());
        }
        return gameList;
    }

    public GameData updateGameUsername(int id, ChessGame.TeamColor color, String username) throws DataAccessException {
        GameData gOld = games.get(id);
        GameData gNew = null;
        switch (color) {
            case BLACK -> gNew = new GameData(id, gOld.whiteUsername(), username, gOld.gameName(), gOld.game());
            case WHITE -> gNew = new GameData(id, username, gOld.blackUsername(), gOld.gameName(), gOld.game());
        }
        games.replace(id, gNew);
        return gNew;
    }

//    public GameData updateGameData(int id, ChessGame game) throws DataAccessException {
//        GameData gOld = games.get(id);
//        GameData gNew = new GameData(id, gOld.whiteUsername(), gOld.blackUsername(), gOld.gameName(), game);
//        games.replace(id, gNew);
//        return gNew;
//    }
}
