package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chess.*;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import model.AuthToken;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import request_response.*;
import service.GetAuthToken;
import service.GetGame;
import service.UpdateGame;
import spark.*;
import com.google.gson.Gson;
import dataAccess.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

@WebSocket
public class Server {

    HashMap<Integer, ArrayList<Session>> gameSessions = new HashMap<>();

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

        Spark.webSocket("/connect", Server.class);

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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.printf("Received: %s", message);
        final RuntimeTypeAdapterFactory<UserGameCommand> gameTypeFactory = RuntimeTypeAdapterFactory
                .of(UserGameCommand.class, "commandType", true)
                .registerSubtype(JoinPlayerCommand.class, String.valueOf(UserGameCommand.CommandType.JOIN_PLAYER))
                .registerSubtype(JoinObserverCommand.class, String.valueOf(UserGameCommand.CommandType.JOIN_OBSERVER))
                .registerSubtype(LeaveCommand.class, String.valueOf(UserGameCommand.CommandType.LEAVE))
                .registerSubtype(MakeMoveCommand.class, String.valueOf(UserGameCommand.CommandType.MAKE_MOVE))
                .registerSubtype(ResignCommand.class, String.valueOf(UserGameCommand.CommandType.RESIGN));
        var builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(gameTypeFactory);
        builder.registerTypeAdapter(ChessGame.class, new GameImpl.ChessGameAdapter());
        builder.registerTypeAdapter(ChessBoard.class, new BoardImpl.ChessBoardAdapter());
        builder.registerTypeAdapter(ChessPiece.class, new PieceImpl.ChessPieceAdapter());
        builder.registerTypeAdapter(ChessMove.class, new MoveImpl.ChessMoveAdapter());
        builder.registerTypeAdapter(ChessPosition.class, new PositionImpl.ChessPositionAdapter());

        var command = builder.create().fromJson(message, UserGameCommand.class);
        switch(command.getCommandType()){
            case JOIN_PLAYER:
                onJoinPlayerMessage(session, (JoinPlayerCommand) command);
                break;
            case JOIN_OBSERVER:
                onJoinObserverMessage(session, (JoinObserverCommand) command);
                break;
            case LEAVE:
                onLeaveMessage(session, (LeaveCommand) command);
                break;
            case MAKE_MOVE:
                onMakeMoveMessage(session, (MakeMoveCommand) command);
                break;
            case RESIGN:
                onResignMessage(session, (ResignCommand) command);
                break;
            default:
                session.getRemote().sendString("WebSocket response: " + message);
                break;
        }
    }

    private Gson getMessageSerializer(){
        final RuntimeTypeAdapterFactory<ServerMessage> gameTypeFactory = RuntimeTypeAdapterFactory
                .of(ServerMessage.class, "serverMessageType")
                .registerSubtype(NotificationMessage.class)
                .registerSubtype(ErrorMessage.class)
                .registerSubtype(LoadGameMessage.class);
        var builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(gameTypeFactory);
        return builder.create();

    }

    private void onJoinPlayerMessage(Session session, JoinPlayerCommand command) throws IOException {
        GetGameResponse res = GetGame.getGame(new GetGameRequest(command.getAuthString(), command.gameID));
        AuthToken token = GetAuthToken.getAuthToken(new GetAuthTokenRequest(command.authToken)).authToken;
        if(res.statusCode.equals(StatusCode.SUCCESS)){
            if((command.playerColor.equals(ChessGame.TeamColor.BLACK) && !token.username.equals(res.game.blackUsername))
                    || (command.playerColor.equals(ChessGame.TeamColor.WHITE) && !token.username.equals(res.game.whiteUsername))){
                session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage("Invalid color")));
                return;
            }
            LoadGameMessage msg = new LoadGameMessage(res.game);
            session.getRemote().sendString(getMessageSerializer().toJson(msg));

            gameSessions.computeIfAbsent(command.gameID, k -> new ArrayList<>());
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            NotificationMessage nMsg = new NotificationMessage(token.username + " has joined the game as " + command.playerColor);
            for(Session s: sessions){
                s.getRemote().sendString(getMessageSerializer().toJson(nMsg));
            }

            sessions.add(session);
        }
        else{
            session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage(res.errorMessage)));
        }
    }

    private void onJoinObserverMessage(Session session, JoinObserverCommand command) throws IOException {
        GetGameResponse res = GetGame.getGame(new GetGameRequest(command.getAuthString(), command.gameID));
        AuthToken token = GetAuthToken.getAuthToken(new GetAuthTokenRequest(command.authToken)).authToken;
        if(res.statusCode.equals(StatusCode.SUCCESS)){

            LoadGameMessage msg = new LoadGameMessage(res.game);
            session.getRemote().sendString(getMessageSerializer().toJson(msg));

            gameSessions.computeIfAbsent(command.gameID, k -> new ArrayList<>());
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            NotificationMessage nMsg = new NotificationMessage(token.username + " has joined as an observer");
            for(Session s: sessions){
                s.getRemote().sendString(getMessageSerializer().toJson(nMsg));
            }
            sessions.add(session);
        }
        else{
            session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage(res.errorMessage)));
        }
    }

    private void onLeaveMessage(Session session, LeaveCommand command) throws IOException {
        //Server sends a Notification message to all other clients in that game informing them that the root client left. Everyone but the root client should be notified.
        GetGameResponse res = UpdateGame.leave(new LeaveCommand(command.getAuthString(), command.gameID));
        AuthToken token = GetAuthToken.getAuthToken(new GetAuthTokenRequest(command.authToken)).authToken;
        if(res.statusCode.equals(StatusCode.SUCCESS)){
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            sessions.remove(session);
            NotificationMessage nMsg = new NotificationMessage(token.username + " has left");
            for(Session s: sessions){
                s.getRemote().sendString(getMessageSerializer().toJson(nMsg));
            }
        }
        else{
            session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage(res.errorMessage)));
        }
    }

    private void onMakeMoveMessage(Session session, MakeMoveCommand command) throws Exception {
        GetGameResponse res = UpdateGame.makeMove(command);
        if(res.statusCode.equals(StatusCode.SUCCESS)){
            //Server sends a LOAD_GAME message to all clients in the game (including the root client) with an updated game.
            LoadGameMessage msg = new LoadGameMessage(res.game);
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            NotificationMessage nMsg = new NotificationMessage("Move made. Now " + res.game.game.getTeamTurn() + "'s turn");
            for(Session s: sessions){
                s.getRemote().sendString(getMessageSerializer().toJson(msg));
                if(!s.equals(session)) {
                    //Server sends a Notification message to all other clients in that game informing them what move was made.
                    s.getRemote().sendString(getMessageSerializer().toJson(nMsg));
                }
                if(res.game.game.isInCheckmate(res.game.game.getTeamTurn())){
                    s.getRemote().sendString(getMessageSerializer().toJson(new NotificationMessage(res.game.game.getTeamTurn() + " is in CHECKMATE!!")));
                }
                else if(res.game.game.isInCheck(res.game.game.getTeamTurn())){
                    s.getRemote().sendString(getMessageSerializer().toJson(new NotificationMessage(res.game.game.getTeamTurn() + " is in CHECK")));
                }
            }
        }
        else{
            session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage(res.errorMessage)));
        }
    }

    private void onResignMessage(Session session, ResignCommand command) throws IOException {
        GetGameResponse res = UpdateGame.resign(command);
//      Server sends a Notification message to all clients in that game informing them that the root client resigned. This applies to both players and observers.
        if(res.statusCode.equals(StatusCode.SUCCESS)){
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            NotificationMessage nMsg = new NotificationMessage(res.game.game.getTeamTurn() + " has RESIGNED :(");
            for(Session s: sessions){
                s.getRemote().sendString(getMessageSerializer().toJson(nMsg));
            }
        }
        else{
            session.getRemote().sendString(getMessageSerializer().toJson(new ErrorMessage(res.errorMessage)));
        }
    }

}
