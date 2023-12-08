package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthToken;
import model.Game;
import request_response.GetGameResponse;
import request_response.StatusCode;
import webSocketMessages.userCommands.LeaveCommand;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.userCommands.ResignCommand;

import java.util.Collection;

public class UpdateGame {
    public static GetGameResponse makeMove(MakeMoveCommand command) {
        //Server verifies the validity of the move.
        try {
            AuthToken authToken = AuthDAO.getToken(command.authToken);
            Game game = GameDAO.getGame(command.gameID);
            if(!game.complete){
                if((authToken.username.equals(game.blackUsername) && game.game.getTeamTurn().equals(ChessGame.TeamColor.BLACK))
                        || ((authToken.username.equals(game.whiteUsername) && game.game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)))){
                    //Game is updated to represent the move. Game is updated in the database.
                    game.game.makeMove(command.move);
                    if(game.game.isInCheckmate(game.game.getTeamTurn())){
                        game.complete = true;
                    }
                    GameDAO.updateGame(game);
                    return new GetGameResponse(game);
                }
                else{
                    return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
                }
            }
            else{
                return new GetGameResponse(StatusCode.BAD_REQUEST, "Error: invalid move, game is over");
            }
        }
        catch(InvalidMoveException e){
            return new GetGameResponse(StatusCode.BAD_REQUEST, "Error: invalid move");
        }
        catch (DataAccessException e) {
            return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }

    public static GetGameResponse resign(ResignCommand command){
        // Server marks the game as over (no more moves can be made). Game is updated in the database.
        try {
            AuthToken authToken = AuthDAO.getToken(command.authToken);
            Game game = GameDAO.getGame(command.gameID);
            if(!game.complete){
                if(authToken.username.equals(game.blackUsername) || authToken.username.equals(game.whiteUsername)){
                    //Game is updated to represent the move. Game is updated in the database.
                    game.complete = true;
                    GameDAO.updateGame(game);
                    return new GetGameResponse(game);
                }
                else{
                    return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
                }
            }
            else {
                return new GetGameResponse(StatusCode.BAD_REQUEST, "Error: invalid move, game is over");
            }
        }
        catch (DataAccessException e) {
            return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }

    public static GetGameResponse leave(LeaveCommand command){
        //updated the game to remove the root client. Game is updated in the database (if client is a player).
        try {
            AuthToken authToken = AuthDAO.getToken(command.authToken);
            Game game = GameDAO.getGame(command.gameID);
            if(authToken.username.equals((game.whiteUsername))){
                game.whiteUsername = null;
                GameDAO.updateGame(game);
                return new GetGameResponse(game);
            }
            else if(authToken.username.equals((game.blackUsername))){
                game.blackUsername = null;
                GameDAO.updateGame(game);
                return new GetGameResponse(game);
            }
            else {
                return new GetGameResponse(game); //no update needed, just return the game
            }
        }
        catch(DataAccessException e){
            return new GetGameResponse(StatusCode.UNAUTHORIZED, "Error: unauthorized");
        }
    }
}
