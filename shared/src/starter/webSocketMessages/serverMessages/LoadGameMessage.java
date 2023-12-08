package webSocketMessages.serverMessages;

import chess.GameImpl;
import model.Game;

public class LoadGameMessage extends ServerMessage{
    public Game game;
    public LoadGameMessage(Game game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
