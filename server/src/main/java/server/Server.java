package server;

import com.google.gson.Gson;
import dataAccess.*;
import exceptions.UnauthorizedException;
import exceptions.UserAlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Collection;

record LoginRequest(String username, String password) {}
record ListGamesResponse(Collection<GameData> games) {}
record CreateGameRequest(String gameName) {}
record CreateGameResponse(int gameID) {}

public class Server {

    Gson serializer = new Gson();

    // DAOs
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();
    UserDAO userDao = new MemoryUserDAO();

    // Services
    ClearService clearService = new ClearService(gameDao, authDao, userDao);
    UserService userService = new UserService(authDao, userDao);
    GameService gameService = new GameService(authDao, userDao, gameDao);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    Object clearHandler(Request req, Response res) {
        try {
            clearService.clear();
        } catch (DataAccessException e) {
            dataAccessError(res);
        }
        return "";
    }

    Object registerHandler(Request req, Response res) {
        UserData u = serializer.fromJson(req.body(), UserData.class);
        if (u.username() == null || u.password() == null || u.email() == null) {
            return badRequest(res);
        }
        try {
            AuthData a = userService.register(u);
            return serializer.toJson(a);
        } catch (DataAccessException e) {
            res.status(400);
            return badRequest(res);
        } catch (UserAlreadyTakenException e) {
            res.status(403);
            return errorJson("already taken");
        }
    }

    Object loginHandler(Request req, Response res) {
        LoginRequest lr = serializer.fromJson(req.body(), LoginRequest.class);
        if (lr.username() == null || lr.password() == null) {
            return badRequest(res);
        }
        try {
            AuthData a = userService.login(lr.username(), lr.password());
            return serializer.toJson(a);
        } catch (UnauthorizedException e) {
            return unauthorizedError(res);
        } catch (DataAccessException e) {
            return dataAccessError(res);
        }
    }

    Object logoutHandler(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            userService.logout(authToken);
            return "";
        } catch (DataAccessException e) {
            return unauthorizedError(res);
        }
    }

    Object listGamesHandler(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            Collection<GameData> games = gameService.listGames(authToken);
            return serializer.toJson(new ListGamesResponse(games));
        } catch (UnauthorizedException e) {
            return unauthorizedError(res);
        }
    }

    Object createGameHandler(Request req, Response res) {
        String authToken = req.headers("authorization");
        CreateGameRequest cg = serializer.fromJson(req.body(), CreateGameRequest.class);
        if (authToken == null || cg.gameName() == null) {
            return badRequest(res);
        }
        try {
            int gameID = gameService.createGame(authToken, cg.gameName());
            return serializer.toJson(new CreateGameResponse(gameID));
        } catch (UnauthorizedException e) {
            return unauthorizedError(res);
        }
    }

    String errorJson(String message) {
        return "{ \"message\": \"Error: " + message + "\" }";
    }

    String badRequest(Response res) {
        res.status(400);
        return errorJson("bad request");
    }

    String unauthorizedError(Response res) {
        res.status(401);
        return errorJson("unauthorized");
    }

    String dataAccessError(Response res) {
        res.status(500);
        return errorJson("data access");
    }
}
