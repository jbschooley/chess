package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.SqlAuthDAO;
import dataAccess.SqlUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SqlAuthDAOTests {

    SqlAuthDAO authDao;
    SqlUserDAO userDao;

    String username = "testuser";
    String password = "testpass";
    String email = "a@b.c";
    UserData u;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDao = new SqlAuthDAO();
        userDao = new SqlUserDAO();
        authDao.clear();
        userDao.clear();
        // create user
        UserData u = new UserData(username, password, email);
        userDao.createUser(u);
    }

    @Test
    public void clear() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> authDao.clear());
    }

    @Test
    public void createAuth() throws DataAccessException {
        AuthData a = authDao.createAuth(username);
        Assertions.assertEquals(a.username(), username);
        Assertions.assertNotNull(a.authToken());
    }

    @Test
    public void createAuthFailInvalidUsername() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> authDao.createAuth("fakeuser"));
    }

    @Test
    public void getAuth() throws DataAccessException {
        AuthData a = authDao.createAuth(username);
        AuthData a2 = authDao.getAuth(a.authToken());
        Assertions.assertEquals(a, a2);
    }

    @Test
    public void getAuthFailInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> authDao.getAuth(username));
    }

    @Test
    public void deleteAuth() throws DataAccessException {
        AuthData a = authDao.createAuth(username);
        Assertions.assertDoesNotThrow(() -> authDao.deleteAuth(a.authToken()));
    }

    @Test
    public void deleteAuthFailInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> authDao.deleteAuth("faketoken"));
    }

}
