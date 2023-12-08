import chess.*;
import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import model.AuthToken;
import request_response.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade extends Endpoint {
    private URI uri;
    private Session session;

 static class ResponseObj<T>{
     public StatusCode responseCode;
     public String errorMessage;
     public T body;

 }

 public ServerFacade(URI uri, ServerListener listener) throws Exception {
     this.uri = uri;

     WebSocketContainer container = ContainerProvider.getWebSocketContainer();
     this.session = container.connectToServer(this, new URI("ws://localhost:8080/connect"));

     this.session.addMessageHandler(new MessageHandler.Whole<String>() {
         public void onMessage(String message) {

             final RuntimeTypeAdapterFactory<ServerMessage> serverMessageFactory = RuntimeTypeAdapterFactory
                     .of(ServerMessage.class, "serverMessageType", true)
                     .registerSubtype(NotificationMessage.class, String.valueOf(ServerMessage.ServerMessageType.NOTIFICATION))
                     .registerSubtype(ErrorMessage.class, String.valueOf(ServerMessage.ServerMessageType.ERROR))
                     .registerSubtype(LoadGameMessage.class, String.valueOf(ServerMessage.ServerMessageType.LOAD_GAME));


             var builder = new GsonBuilder();
             builder.registerTypeAdapter(ChessGame.class, new GameImpl.ChessGameAdapter());
             builder.registerTypeAdapter(ChessBoard.class, new BoardImpl.ChessBoardAdapter());
             builder.registerTypeAdapter(ChessPiece.class, new PieceImpl.ChessPieceAdapter());

             builder.registerTypeAdapterFactory(serverMessageFactory);

             var response = builder.create().fromJson(message, ServerMessage.class);
             listener.onServerMessage(response);
         }
     });
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
        var map = new HashMap<String, Object>();
        map.put("gameID", req.gameID);
        if(req.playerColor != null){
            map.put("playerColor", req.playerColor);
        }
        var body = new Gson().toJson(map);
        var httpConnection = sendRequest(uri + "/game", "PUT", body, req.authToken);

        ResponseObj<JoinGameResponse> resObj = receiveResponse(httpConnection, JoinGameResponse.class);
        if(resObj.responseCode == StatusCode.SUCCESS){
            if(req.playerColor == null){
                JoinObserverCommand command = new JoinObserverCommand(req.authToken, req.gameID);
                var msg = new Gson().toJson(command);
                this.send(msg);
            }
            else{
                JoinPlayerCommand command = new JoinPlayerCommand(req.authToken, req.gameID, req.playerColor);
                var msg = new Gson().toJson(command);
                this.send(msg);
            }
            return resObj.body;
        }
        else{
            return new JoinGameResponse(resObj.responseCode, resObj.errorMessage);
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, move);
        var msg = new Gson().toJson(command);
        this.send(msg);
    }

    public void resign(String authToken, int gameID) throws Exception {
        ResignCommand command = new ResignCommand(authToken, gameID);
        var msg = new Gson().toJson(command);
        this.send(msg);
    }

    public void leave(String authToken, int gameID) throws Exception {
        LeaveCommand command = new LeaveCommand(authToken, gameID);
        var msg = new Gson().toJson(command);
        this.send(msg);
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

    private void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
