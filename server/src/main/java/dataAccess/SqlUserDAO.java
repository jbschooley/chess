package dataAccess;

import model.UserData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;


public class SqlUserDAO implements UserDAO {

    Collection<UserData> users = new HashSet<UserData>();

    public SqlUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = """
                TRUNCATE TABLE user;
                TRUNCATE TABLE game;
                TRUNCATE TABLE auth;
                """;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            throw new DataAccessException("SQL error");
        }
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

    public boolean checkPassword(String username, String password) {
        UserData u = null;
        try {
            u = getUser(username);
            return u.password().equals(password);
        } catch (DataAccessException e) {
            return false;
        }
    }
}
