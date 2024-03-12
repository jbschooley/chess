package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.util.*;

public class SqlGameDAO implements GameDAO {

    Gson serializer = new Gson();
    SortedMap<Integer, GameData> games = new TreeMap<Integer, GameData>();

    public void clear() throws DataAccessException {
        String statement = "DELETE FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public GameData createGame(String name) throws DataAccessException {
        ChessGame cg = new ChessGame();

        String statementInsert = "INSERT INTO game (gameName, game) VALUES (?, ?);";
        String statementGetLast = "SELECT LAST_INSERT_ID() AS gameID;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatementInsert = conn.prepareStatement(statementInsert);
                 var preparedStatementGetLast = conn.prepareStatement(statementGetLast)) {
                preparedStatementInsert.setString(1, name);
                preparedStatementInsert.setString(2, serializer.toJson(cg));
                conn.setAutoCommit(false);
                var rs1 = preparedStatementInsert.executeUpdate();
                var rs2 = preparedStatementGetLast.executeQuery();
                conn.commit();
                if (rs2.next()) {
                    return new GameData(rs2.getInt("gameID"), null, null, name, cg);
                } else {
                    throw new DataAccessException("Query error, could not create game");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public GameData getGame(int id) throws DataAccessException {
        String statement = "SELECT * FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, id);
                var rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    ChessGame g = serializer.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            g);
                } else {
                    throw new DataAccessException("User not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public Collection<GameData> listGames() throws DataAccessException {

        String statement = "SELECT * FROM game ORDER BY gameID";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                Collection<GameData> gameList = new HashSet<>();
                while (rs.next()) {
                    ChessGame g = serializer.fromJson(rs.getString("game"), ChessGame.class);
                    gameList.add(new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            g));
                }
                return gameList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }
    }

    public GameData updateGameUsername(int id, ChessGame.TeamColor color, String username) throws DataAccessException {
        String colorField;
        switch (color) {
            case WHITE -> colorField = "whiteUsername";
            case BLACK -> colorField = "blackUsername";
            default -> throw new DataAccessException("Invalid color");
        }
        String statement = "UPDATE game SET " + colorField + " = ? WHERE gameID = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setInt(2, id);
                var rs = preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("SQL error");
        }

        return getGame(id);
    }
}
