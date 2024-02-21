package dataAccess;

import model.UserData;

import java.util.Collection;
import java.util.HashSet;


public class MemoryUserDAO implements UserDAO {

    Collection<UserData> users = new HashSet<UserData>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    public UserData createUser(UserData u) throws DataAccessException {
        users.add(u);
        return u;
    }

    public UserData getUser(String username) throws DataAccessException {
        // yes I know this is inefficient, but it'll do the job
        for (UserData u : users) {
            if (u.username().equals(username)) {
                return u;
            }
        }
        throw new DataAccessException("User not found");
    }

    public boolean checkPassword(String username, String password) throws DataAccessException {
        UserData u = getUser(username);
        return u.password().equals(password);
    }
}
