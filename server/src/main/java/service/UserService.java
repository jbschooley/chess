package service;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exceptions.UnauthorizedException;
import exceptions.UserAlreadyTakenException;
import model.AuthData;
import model.UserData;

public class UserService {

    AuthDAO authDao;
    UserDAO userDao;


    public UserService(AuthDAO authDao, UserDAO userDao) {
        this.authDao = authDao;
        this.userDao = userDao;
    }

    public AuthData register(UserData user) throws DataAccessException, UserAlreadyTakenException {
        try {
            userDao.getUser(user.username());
            throw new UserAlreadyTakenException();
        } catch (DataAccessException e) {
            // user does not exist, so create new
            userDao.createUser(user);
            return authDao.createAuth(user.username());
        }
    }

    public AuthData login(String username, String password) throws DataAccessException, UnauthorizedException {
        if (userDao.checkPassword(username, password)) {
            return authDao.createAuth(username);
        } else {
            throw new UnauthorizedException();
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authDao.deleteAuth(authToken);
    }
}

