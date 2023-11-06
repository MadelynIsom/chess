package server;

import java.util.Map;
import spark.*;
import com.google.gson.Gson;
import dataAccess.*;

import javax.xml.crypto.Data;

public class Server {

    public static void main(String[] args) {
        new Server().run();
    }

    private void run() {
        // Specify the port you want the server to listen on
        Spark.port(8080);

        // Register a directory for hosting static files
        Spark.externalStaticFileLocation("web");

        Spark.exception(Exception.class, this::errorHandler);
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new Exception(msg), req, res);
        });

        Spark.delete("/db", Handlers::handleClear);
        Spark.post("/user", Handlers::handleRegister);
        Spark.post("/session", Handlers::handleLogin);
        Spark.delete("/session", Handlers::handleLogout);
        Spark.get("/game", Handlers::handleListGames);
        Spark.post("/game", Handlers::handleCreateGame);
        Spark.put("/game", Handlers::handleJoinGame);
    }

    public Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }

}
