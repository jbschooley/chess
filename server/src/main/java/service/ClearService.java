package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;

public class ClearService {

    GameDAO gameDao;
    AuthDAO authDao;
    UserDAO userDao;

    public ClearService(GameDAO gameDao, AuthDAO authDao, UserDAO userDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
        this.userDao = userDao;
    }

    public void clear() throws DataAccessException {
        gameDao.clear();
        authDao.clear();
        userDao.clear();
    }
}
