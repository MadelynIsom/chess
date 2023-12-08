package model;

import chess.GameImpl;
/**
 * represents a model object of a Game
 * stores a gameID number,
 * a username for the white team player,
 * a username for the black team player,
 * a game name,
 * and a game object
 */
public class Game {
    /**
     * a unique ID for the game
     */
    public int gameID;
    /**
     * the username of the white team player
     */
    public String whiteUsername;
    /**
     * the username of the black team player
     */
    public String blackUsername;
    /**
     * the name of the game
     */
    public String gameName;
    /**
     * the game object implementation
     */
    public GameImpl game;

    public boolean complete;

    public Game(int gameID, String whiteUsername, String blackUsername, String gameName, GameImpl game, boolean complete){
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
        this.complete = complete;
    }

    public Game(String whiteUsername, String blackUsername, String gameName, GameImpl game){
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
        this.complete = false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Game foreignGame){
            return (gameID == foreignGame.gameID && whiteUsername.equals(foreignGame.whiteUsername) &&
                    blackUsername.equals(foreignGame.blackUsername) && gameName.equals(foreignGame.gameName) &&
                    game.equals(foreignGame.game));
        }
        return false;
    }
}
