package dataAccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
    UserData createUser(UserData u) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean checkPassword(String username, String password) throws DataAccessException;
}
