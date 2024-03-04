package dataAccess;

import model.UserData;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;


public class SqlUserDAO implements UserDAO {

    Collection<UserData> users = new HashSet<UserData>();

    public SqlUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM user";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public UserData createUser(UserData u) throws DataAccessException {
        String statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, u.username());
                preparedStatement.setString(2, u.password()); // TODO bcrypt
                preparedStatement.setString(3, u.email());
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
        return u;
    }

    public UserData getUser(String username) throws DataAccessException {
        String statement = "SELECT * FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                } else {
                    throw new DataAccessException("User not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public boolean checkPassword(String username, String password) throws DataAccessException {
        String statement = "SELECT * FROM user WHERE username = ? AND password = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password); // TODO bcrypt
                var rs = preparedStatement.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }
}
