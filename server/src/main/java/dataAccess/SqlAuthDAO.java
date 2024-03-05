package dataAccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO {

    Collection<AuthData> auths = new HashSet<AuthData>();

    @Override
    public void clear() throws DataAccessException {
        String statement = "DELETE FROM auth";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();

        String statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, token);
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }

        AuthData a = new AuthData(token, username);
        return a;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        String statement = "SELECT * FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                } else {
                    throw new DataAccessException("Auth token does not exist");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                var rs = preparedStatement.executeUpdate();
                if (rs == 0) throw new DataAccessException("Auth token does not exist");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }
}
