import webSocketMessages.serverMessages.ServerMessage;

public interface ServerListener {
    void onServerMessage(ServerMessage message);
}
