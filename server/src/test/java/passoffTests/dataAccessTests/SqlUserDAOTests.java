package passoffTests.dataAccessTests;
import exceptions.UnauthorizedException;
import model.UserData;
import org.junit.jupiter.api.*;
import dataAccess.DataAccessException;
import dataAccess.SqlUserDAO;
import dataAccess.UserDAO;

public class SqlUserDAOTests {

    UserDAO userDao;

    String username = "testuser";
    String password = "testpass";
    String email = "a@b.c";

    @BeforeEach
    public void setup() throws DataAccessException {
        userDao = new SqlUserDAO();
        userDao.clear();
    }

    @Test
    public void clear() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> userDao.clear());
    }

    @Test
    public void createUser() {
        UserData u = new UserData(username, password, email);
        Assertions.assertDoesNotThrow(() -> userDao.createUser(u));
    }

    @Test
    public void createUserFailDuplicate() throws DataAccessException {
        UserData u = new UserData(username, password, email);
        userDao.createUser(u);
        Assertions.assertThrows(DataAccessException.class, () -> userDao.createUser(u));
    }

    @Test
    public void getUser() throws DataAccessException, UnauthorizedException {
        UserData u = new UserData(username, password, email);
        userDao.createUser(u);
        Assertions.assertEquals(u.username(), userDao.getUser(username).username());
    }

    @Test
    public void getUserFailInvalid() {
        Assertions.assertThrows(UnauthorizedException.class, () -> userDao.getUser(username));
    }

    @Test
    public void checkPassword() throws DataAccessException, UnauthorizedException {
        UserData u = new UserData(username, password, email);
        userDao.createUser(u);
        Assertions.assertTrue(userDao.checkPassword(username, password));
    }

    @Test
    public void checkPasswordFail() throws DataAccessException, UnauthorizedException {
        UserData u = new UserData(username, password, email);
        userDao.createUser(u);
        Assertions.assertFalse(userDao.checkPassword(username, "wrong"));
    }


}
