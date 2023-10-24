package service;

import model.Game;
import java.util.ArrayList;

/**
 * represents a ListGameResponse from the server
 * stores an array of game models (if successful), a statusCode enum, and an errorMessage (if an error was thrown)
 */

public class ListGamesResponse {
    /**
     * the list of all games
     */
    public ArrayList<Game> games;
    /**
     * the status code indicating success or failure
     */
    public StatusCode statusCode;
    /**
     * the error message if operation fails
     */
    public String errorMessage;

    /**
     * constructor for a successful operation
     * @param games games list
     */
    public ListGamesResponse(ArrayList<Game> games){
        this.games = games;
        this.statusCode = StatusCode.SUCCESS;
        this.errorMessage = null;
    }
    /**
     * constructor for an unsuccessful operation
     * @param statusCode error code
     * @param errorMessage error message
     */
    public ListGamesResponse(StatusCode statusCode, String errorMessage){
        this.games = null;
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
