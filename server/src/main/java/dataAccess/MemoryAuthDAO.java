package dataAccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    Collection<AuthData> auths = new HashSet<AuthData>();

    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData a = new AuthData(token, username);
        auths.add(a);
        return a;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData a : auths) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }
        throw new DataAccessException("Auth token does not exist");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData a = getAuth(authToken);
        auths.remove(a);
    }
}
