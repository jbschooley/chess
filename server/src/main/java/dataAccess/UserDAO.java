package dataAccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    UserData createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean checkPassword(String username, String password) throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
}
