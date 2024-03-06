package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;

public class SqlGameDAOTests {

    SqlAuthDAO authDao;
    SqlUserDAO userDao;
    SqlGameDAO gameDAO;

    String username = "testuser";
    String password = "testpass";
    String email = "a@b.c";
    UserData u;
    AuthData a;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDao = new SqlAuthDAO();
        userDao = new SqlUserDAO();
        gameDAO = new SqlGameDAO();
        authDao.clear();
        userDao.clear();
        gameDAO.clear();
        // create user
        u = new UserData(username, password, email);
        userDao.createUser(u);
        // create auth
        a = authDao.createAuth(username);
    }

    @Test
    public void clear() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> gameDAO.clear());
    }

    @Test
    public void createGame() throws DataAccessException {
        String name = "TestGame";
        GameData g = gameDAO.createGame(name);
        Assertions.assertEquals(g.gameName(), name);
        Assertions.assertNotNull(g.game());
    }

    @Test
    public void createGameFail() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGame() throws DataAccessException {
        String name = "TestGame";
        GameData g = gameDAO.createGame(name);
        GameData g2 = gameDAO.getGame(g.gameID());
        Assertions.assertEquals(g, g2);
    }

    @Test
    public void getGameFailInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(-1));
    }

    @Test
    public void listGames() throws DataAccessException {
        GameData g = gameDAO.createGame("TestGame");
        Collection<GameData> gameList = gameDAO.listGames();
        Assertions.assertEquals(gameList.iterator().next(), g);
    }

    @Test
    public void listGamesFailNoGames() throws DataAccessException {
        Collection<GameData> gameList = gameDAO.listGames();
        Assertions.assertTrue(gameList.isEmpty());
    }

    @Test
    public void updateGameUsername() throws DataAccessException {
        GameData g = gameDAO.createGame("TestGame");
        gameDAO.updateGameUsername(g.gameID(), ChessGame.TeamColor.WHITE, u.username());
        GameData g2 = gameDAO.getGame(g.gameID());
        Assertions.assertEquals(u.username(), g2.whiteUsername());
    }

    @Test
    public void updateGameUsernameFailInvalidUsername() throws DataAccessException {
        GameData g = gameDAO.createGame("TestGame");
        Assertions.assertThrows(DataAccessException.class,
                () -> gameDAO.updateGameUsername(g.gameID(), ChessGame.TeamColor.WHITE, "fakeUser"));


    }

}
