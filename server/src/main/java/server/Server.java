package server;

import com.google.gson.Gson;
import dataAccess.*;
import exceptions.UserAlreadyTakenException;
import model.AuthData;
import model.UserData;
import service.ClearService;
import service.UserService;
import spark.*;

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

        Spark.post("/user", this::register);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    Object clearHandler(Request req, Response res) throws DataAccessException {
        clearService.clear();
        return "";
    }

    Object register(Request req, Response res) throws DataAccessException {
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

    String errorJson(String message) {
        return "{ \"message\": \"Error: " + message + "\" }";
    }

    String badRequest(Response res) {
        res.status(400);
        return errorJson("bad request");
    }
}
