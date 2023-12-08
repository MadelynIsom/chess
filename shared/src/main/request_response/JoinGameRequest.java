package request_response;

import chess.ChessGame;

/**
 * represents a request from the client to join a game
 * stores the color the user wants and the ID of the game being joined
 */
public class JoinGameRequest {
    /**
     * the authToken to authenticate the request
     */
    public String authToken;
    /**
     * the team color the user wants to play as
     */
    public ChessGame.TeamColor playerColor;
    /**
     * the unique ID for the game
     */
    public int gameID;

    public JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameID){
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameID = gameID;
    }
}
