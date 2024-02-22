package server;

import com.google.gson.Gson;
import dataAccess.*;
import exceptions.UnauthorizedException;
import exceptions.UserAlreadyTakenException;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.UserService;
import spark.*;

record LoginRequest(String username, String password) {}

public class Server {

    Gson serializer = new Gson();

    // DAOs
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();
    UserDAO userDao = new MemoryUserDAO();

    // Services
    ClearService clearService = new ClearService(gameDao, authDao, userDao);
    UserService userService = new UserService(authDao, userDao);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearHandler);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);

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
            res.status(500);
            return "{ \"message\": \"Error: description\" }";
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
            res.status(401);
            return errorJson("unauthorized");
        } catch (DataAccessException e) {
            return descriptionError(res);
        }
    }

    Object logoutHandler(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            userService.logout(authToken);
            return "";
        } catch (DataAccessException e) {
            res.status(401);
            return errorJson("unauthorized");
        }
    }

    String errorJson(String message) {
        return "{ \"message\": \"Error: " + message + "\" }";
    }

    String badRequest(Response res) {
        res.status(400);
        return errorJson("bad request");
    }

    String descriptionError(Response res) {
        res.status(500);
        return errorJson("description");
    }
}
