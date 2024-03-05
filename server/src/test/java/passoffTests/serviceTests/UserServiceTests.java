package passoffTests.serviceTests;

import dataAccess.*;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import exceptions.UserAlreadyTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import service.ClearService;
import service.GameService;
import service.UserService;

public class UserServiceTests {

    // DAOs
    GameDAO gameDao;
    AuthDAO authDao;
    UserDAO userDao;

    ClearService clearService = new ClearService(gameDao, authDao, userDao);
    UserService userService = new UserService(authDao, userDao);

    // test user
    String testUserUsername = "testuser";
    String testUserPassword = "testpass";
    String testUserEmail = "a@b.c";

    @BeforeEach
    public void setup() throws TestException, DataAccessException {

        // Initialize DAOs and services
        try {
            userDao = new SqlUserDAO();
            authDao = new SqlAuthDAO();
            gameDao = new SqlGameDAO();

            clearService = new ClearService(gameDao, authDao, userDao);
            userService = new UserService(authDao, userDao);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        clearService.clear();

        // create test user
        UserData u = new UserData(testUserUsername, testUserPassword, testUserEmail);
        userDao.createUser(u);
    }

    @Test
    @Order(1)
    @DisplayName("Register New User")
    public void registerNewUser() throws TestException, DataAccessException, UserAlreadyTakenException, UnauthorizedException {
        String newUsername = "testUser2";
        UserData u = new UserData(newUsername, testUserPassword, testUserEmail);

        // create new user
        userService.register(u);

        // test user exists
        UserData uTest = new UserData(newUsername, testUserPassword, testUserEmail);
        Assertions.assertEquals(userDao.getUser(newUsername).username(), u.username());
        Assertions.assertEquals(userDao.getUser(newUsername).email(), u.email());
    }

    @Test
    @Order(2)
    @DisplayName("Register Duplicate User")
    public void registerDuplicateUser() throws TestException {
        // try registering same user again
        Assertions.assertThrows(UserAlreadyTakenException.class, () -> {
            UserData u = new UserData(testUserUsername, testUserPassword, testUserEmail);
            userService.register(u);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Login User")
    public void loginUser() throws TestException, UnauthorizedException, DataAccessException {
        // login user with correct password
        AuthData a = userService.login(testUserUsername, testUserPassword);

        // test that authdata contains token
        Assertions.assertNotNull(a.authToken());
    }

    @Test
    @Order(4)
    @DisplayName("Login Wrong Password")
    public void loginUserWrongPassword() throws TestException {
        // login user with wrong password
        Assertions.assertThrows(UnauthorizedException.class, () -> {
            AuthData a = userService.login(testUserUsername, "nope");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Logout")
    public void logout() throws TestException, UnauthorizedException, DataAccessException {
        // login user with correct password
        AuthData a = userService.login(testUserUsername, testUserPassword);

        // logout
        Assertions.assertDoesNotThrow(() ->userService.logout(a.authToken()));
    }

    @Test
    @Order(6)
    @DisplayName("Logout Non-Existent Token")
    public void logoutNonExistentToken() throws TestException, UnauthorizedException, DataAccessException {
        // logout
        Assertions.assertThrows(DataAccessException.class, () ->userService.logout("faketoken"));
    }
}
