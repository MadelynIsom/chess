import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import model.AuthToken;
import request_response.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade { URI uri;

 static class ResponseObj<T>{
     public StatusCode responseCode;
     public String errorMessage;
     public T body;

 }

 public ServerFacade(URI uri){
     this.uri = uri;
 }

    public void clearDatabase() throws Exception {
        var httpConnection = sendRequest(uri + "/db", "DELETE", null, null);
        httpConnection.getResponseCode();
        //System.out.printf("= Response =========\n[%d]\n\n", httpConnection.getResponseCode());
    }

 public RegisterResponse register(RegisterRequest req) throws Exception {
     var body = new Gson().toJson(Map.of("username", req.username ,"password", req.password, "email", req.email));
     var httpConnection = sendRequest(uri + "/user", "POST", body, null);

     ResponseObj<AuthToken> resObj = receiveResponse(httpConnection, AuthToken.class);
     if(resObj.responseCode == StatusCode.SUCCESS){
         return new RegisterResponse(resObj.body);
     }
     else{
         return new RegisterResponse(resObj.responseCode, resObj.errorMessage);
     }
 }

    public LoginResponse login(LoginRequest req) throws Exception {
        var body = new Gson().toJson(Map.of("username", req.username ,"password", req.password));
        var httpConnection = sendRequest(uri + "/session", "POST", body, null);

        ResponseObj<AuthToken> resObj = receiveResponse(httpConnection, AuthToken.class);
        if(resObj.responseCode == StatusCode.SUCCESS){
            return new LoginResponse(resObj.body);
        }
        else{
            return new LoginResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    public LogoutResponse logout(LogoutRequest req) throws Exception {
        var httpConnection = sendRequest(uri + "/session", "DELETE", null, req.authToken);

        ResponseObj<AuthToken> resObj = receiveResponse(httpConnection, null);
        if(resObj.responseCode == StatusCode.SUCCESS){
            return new LogoutResponse(resObj.responseCode);
        }
        else{
            return new LogoutResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    public CreateGameResponse createGame(CreateGameRequest req) throws Exception {
        var body = new Gson().toJson(Map.of("gameName", req.gameName));
        var httpConnection = sendRequest(uri + "/game", "POST", body, req.authToken);

        ResponseObj<CreateGameResponse> resObj = receiveResponse(httpConnection, CreateGameResponse.class);
        if(resObj.responseCode == StatusCode.SUCCESS){
            return resObj.body;
        }
        else{
            return new CreateGameResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    public ListGamesResponse listGames(ListGamesRequest req) throws Exception {
        var httpConnection = sendRequest(uri + "/game", "GET", null, req.authToken);

        ResponseObj<ListGamesResponse> resObj = receiveResponse(httpConnection, ListGamesResponse.class);
        if(resObj.responseCode == StatusCode.SUCCESS){
            return resObj.body;
        }
        else{
            return new ListGamesResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    public JoinGameResponse joinGame(JoinGameRequest req) throws Exception {
        var body = new Gson().toJson(Map.of("playerColor", req.playerColor, "gameID", req.gameID));
        var httpConnection = sendRequest(uri + "/game", "PUT", body, req.authToken);

        ResponseObj<JoinGameResponse> resObj = receiveResponse(httpConnection, JoinGameResponse.class);
        if(resObj.responseCode == StatusCode.SUCCESS){
            return resObj.body;
        }
        else{
            return new JoinGameResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    private HttpURLConnection sendRequest(String url, String method, String body, String authToken) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);
        if(authToken != null){
            http.addRequestProperty("authorization", authToken);
        }
        if(body != null){
            http.addRequestProperty("Content-Type", "application/json");
            writeRequestBody(body, http);
        }
        http.connect();
        //System.out.printf("= Request =========\n[%s] %s\n\n%s\n\n", method, url, body);
        return http;
    }

    private void writeRequestBody(String body, HttpURLConnection http) throws IOException {
        if (!body.isEmpty()) {
            http.setDoOutput(true);
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
    }

    private <T>ResponseObj<T> receiveResponse(HttpURLConnection http, Class<T> cls) throws IOException {
        ResponseObj<T> response = new ResponseObj<T>();
        response.responseCode = StatusCode.fromInteger(http.getResponseCode());
        response.errorMessage = http.getResponseMessage();
        if(cls != null && http.getResponseCode() == 200){
            response.body = readResponseBody(http, cls);
        }
        //System.out.printf("= Response =========\n[%d] %s\n\n%s\n\n", response.responseCode.code, response.errorMessage, response.body);
        return response;
    }

    private Gson getSerializer() {
        class GameInstanceCreator implements InstanceCreator<ChessGame> {
            @Override
            public ChessGame createInstance(Type type) {
                return new GameImpl();
            }
        }
        class BoardInstanceCreator implements InstanceCreator<ChessBoard> {
            @Override
            public ChessBoard createInstance(Type type) {
                return new BoardImpl();
            }
        }
        class PieceInstanceCreator implements InstanceCreator<ChessPiece> {
            @Override
            public ChessPiece createInstance(Type type) {
                return new PieceImpl();
            }
        }

        var builder = new GsonBuilder();

        builder.registerTypeAdapter(ChessGame.class, new GameInstanceCreator());
        builder.registerTypeAdapter(ChessBoard.class, new BoardInstanceCreator());
        builder.registerTypeAdapter(ChessPiece.class, new PieceInstanceCreator());
        return builder.create();
    }


    private <T>T readResponseBody(HttpURLConnection http, Class<T> cls) throws IOException {
        T responseBody = null;
        try (InputStream respBody = http.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            responseBody = getSerializer().fromJson(inputStreamReader, cls);
        }
        return responseBody;
    }
}
