package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    AuthDAO authDao;
    UserDAO userDao;
    GameDAO gameDao;

    public GameService(AuthDAO authDao, UserDAO userDao, GameDAO gameDao) {
        this.authDao = authDao;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    public Collection<GameData> listGames(String authToken) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            return gameDao.listGames();
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public GameData createGame(String authToken, String name) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            GameData g = gameDao.createGame(name);
            return g;
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public GameData getGame(String authToken, int gameID) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            return gameDao.getGame(gameID);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public void joinGamePlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) throws UnauthorizedException, AlreadyTakenException {
        try {
            AuthData a = authDao.getAuth(authToken);
            GameData g = gameDao.getGame(gameID);
            if ((playerColor == ChessGame.TeamColor.WHITE && g.whiteUsername() != null && !g.whiteUsername().equals(a.username())) ||
                    (playerColor == ChessGame.TeamColor.BLACK && g.blackUsername() != null && !g.blackUsername().equals(a.username()))) {
                throw new AlreadyTakenException();
            }
            g = gameDao.updateGameUsername(gameID, playerColor, a.username());
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public GameData makeMove(String authToken, int gameID, ChessMove move) throws UnauthorizedException, InvalidMoveException {
        try {
            AuthData a = authDao.getAuth(authToken);
            GameData g = gameDao.getGame(gameID);
            g.game().makeMove(move);
            gameDao.updateGameData(gameID, g.game());
            return g;
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public void leaveGamePlayer(String authToken, int gameID) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            GameData g = gameDao.getGame(gameID);
            if (a.username().equals(g.whiteUsername())) {
                gameDao.updateGameUsername(gameID, ChessGame.TeamColor.WHITE, null);
            } else if (a.username().equals(g.blackUsername())) {
                gameDao.updateGameUsername(gameID, ChessGame.TeamColor.BLACK, null);
            }
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }

    public void resignGame(String authToken, int gameID) throws UnauthorizedException {
        try {
            AuthData a = authDao.getAuth(authToken);
            GameData g = gameDao.getGame(gameID);
            g.game().setGameEnded(true);
            gameDao.updateGameData(gameID, g.game());
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
    }
}
