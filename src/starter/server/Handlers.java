package server;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import service.*;

import java.util.Map;

public class Handlers {

    public static Object handleClear(Request req, Response res){
        ClearApplicationResponse clearApplicationResponse = ClearApplication.clearDatabase();
        res.type("application/json");
        res.status(clearApplicationResponse.statusCode.code);
        if (clearApplicationResponse.statusCode.code != 200) {
            return new Gson().toJson(Map.of("message", String.format("Error: %s", clearApplicationResponse.errorMessage)));
        }
        return new Gson().toJson(clearApplicationResponse);

    }

    public static Object handleRegister(Request req, Response res){
        RegisterRequest bodyObj = getBody(req, RegisterRequest.class);
        RegisterResponse registerResponse = Register.register(bodyObj);
        res.type("application/json");
        res.status(registerResponse.statusCode.code);
        if(registerResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", registerResponse.errorMessage)));

        }
        return new Gson().toJson(registerResponse.authToken);
    }

    public static Object handleLogin(Request req, Response res) {
        LoginRequest bodyObj = getBody(req, LoginRequest.class);
        LoginResponse loginResponse = Login.login(bodyObj);
        res.type("application/json");
        res.status(loginResponse.statusCode.code);
        if(loginResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", loginResponse.errorMessage)));

        }
        return new Gson().toJson(loginResponse.authToken);
    }

    public static Object handleLogout(Request req, Response res) {
        LogoutRequest logoutRequest = new LogoutRequest(req.headers("authorization"));
        LogoutResponse logoutResponse = Logout.logout(logoutRequest);
        res.type("application/json");
        res.status(logoutResponse.statusCode.code);
        if(logoutResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", logoutResponse.errorMessage)));
        }
        return new Gson().toJson(logoutResponse);
    }

    public static Object handleListGames(Request req, Response res){
        ListGamesRequest bodyObj = new ListGamesRequest(req.headers("authorization"));
        ListGamesResponse listGamesResponse = ListGames.listGames(bodyObj);
        res.type("application/json");
        res.status(listGamesResponse.statusCode.code);
        if(listGamesResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", listGamesResponse.errorMessage)));

        }
        return new Gson().toJson(listGamesResponse);
    }

    public static Object handleCreateGame(Request req, Response res){
        CreateGameRequest bodyObj = getBody(req, CreateGameRequest.class);
        bodyObj.authToken = req.headers("authorization");
        CreateGameResponse createGameResponse = CreateGame.createGame(bodyObj);
        res.type("application/json");
        res.status(createGameResponse.statusCode.code);
        if(createGameResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", createGameResponse.errorMessage)));

        }
        return new Gson().toJson(createGameResponse);
    }

    public static Object handleJoinGame(Request req, Response res){
        JoinGameRequest bodyObj = getBody(req, JoinGameRequest.class);
        bodyObj.authToken = req.headers("authorization");
        JoinGameResponse joinGameResponse = JoinGame.joinGame(bodyObj);
        res.type("application/json");
        res.status(joinGameResponse.statusCode.code);
        if(joinGameResponse.statusCode.code != 200){
            return new Gson().toJson(Map.of("message", String.format("Error: %s", joinGameResponse.errorMessage)));

        }
        return new Gson().toJson(joinGameResponse);
    }

    private static <T> T getBody(Request request, Class<T> clazz) {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new RuntimeException("missing required body");
        }
        return body;
    }
}
