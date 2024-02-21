package server;

import dataAccess.*;
import service.ClearService;
import spark.*;

import java.nio.file.Paths;

public class Server {

    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();
    UserDAO userDao = new MemoryUserDAO();
    ClearService clearService = new ClearService(gameDao, authDao, userDao);

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> {
            clearService.clear();
            return "";
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    void ClearHandler() throws DataAccessException {
        clearService.clear();
    }
}
