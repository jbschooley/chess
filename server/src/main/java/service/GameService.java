package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    AuthDAO authDao;
    UserDAO userDao;
    GameDAO gameDao;

    public GameService(AuthDAO authDao, UserDAO userDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            return gameDao.listGames();
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }
}
