import chess.*;
import model.Game;
import request_response.*;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Repl implements ServerListener{
    private ServerFacade server;
    private State state;
    private String authToken;
    private int currGameID;
    private ChessBoard currBoard;
    private ChessGame currGame;
    private ChessGame.TeamColor playerColor;


    public Repl(URI uri) throws Exception {
        this.server = new ServerFacade(uri, this);
        this.state = State.LOGGED_OUT;
        this.playerColor = null;
        this.authToken = null;
    }

    public void run() {
        while (true) {
            try{
            if(state == State.LOGGED_OUT){
                System.out.printf("[" + state + "] >>> ");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                var args = line.split(" ");
                switch (args[0]) {
                    case "register": register(args);
                        break;
                    case "login": login(args);
                        break;
                    case "quit":
                        return;
                    case "help":
                    default: help();
                        break;
                }
            }
            else if(state == State.LOGGED_IN){
                System.out.printf("[" + state + "] >>> ");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                var args = line.split(" ");
                switch(args[0]){
                    case "create": create(args);
                        break;
                    case "list": list();
                        break;
                    case "join": join(args);
                        break;
                    case "observe": join(args);
                        break;
                    case "logout": logout();
                        break;
                    case "quit": return;
                    case "help":
                    default: help();
                        break;
                }
            }
            else if(state == State.IN_GAME){
                System.out.printf("[" + state + "] >>> ");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                var args = line.split(" ");
                switch(args[0]){
                    case "redraw": redraw();
                        break;
                    case "leave": leave();
                        break;
                    case "move": makeMove(args);
                        break;
                    case "options": getMoves(args);
                        break;
                    case "resign": resign();
                        break;
                    case "quit": return;
                    case "help":
                    default: help();
                        break;
                }
            }
            else {
                System.out.printf("[" + state + "] >>> ");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                var args = line.split(" ");
                switch(args[0]){
                    case "redraw": redraw();
                        break;
                    case "leave": leave();
                        break;
                    case "quit": return;
                    case "help":
                    default: help();
                        break;
                }
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        }
    }

    private void help(){
        if(state == State.LOGGED_OUT){
            System.out.printf("register <USERNAME> <PASSWORD> <EMAIL> - to create an account%n");
            System.out.printf("login <USERNAME> <PASSWORD> - to play chess%n");
            System.out.printf("quit - terminate program%n");
            System.out.printf("help - with possible commands%n%n");

        }
        else if (state == State.LOGGED_IN){
            System.out.printf("create <NAME> - a game%n");
            System.out.printf("list - games%n");
            System.out.printf("join <ID> [WHITE|BLACK|empty] - a game%n");
            System.out.printf("observe <ID> - a game%n");
            System.out.printf("logout - when you are done%n");
            System.out.printf("quit - terminate program%n");
            System.out.printf("help - with possible commands%n%n");
        }
        else if (state == State.IN_GAME){
            System.out.print("redraw - to get updated board\n");
            System.out.print("leave - to leave the game\n");
            System.out.print("move <START> <FINISH> [<PROMOTION PIECE TYPE>]- to move a piece from one position to another (EX: move a2 a4, move h7 h8 QUEEN)\n");
            System.out.print("options <POSITION>- to highlight legal moves for the piece at specified position\n");
            System.out.print("resign - to forfeit and end the game\n");
            System.out.printf("quit - terminate program%n");
            System.out.printf("help - with possible commands%n%n");
        }
        else{
            System.out.print("redraw - to get updated board\n");
            System.out.print("leave - to leave the game\n");
            System.out.printf("quit - terminate program%n");
            System.out.printf("help - with possible commands%n%n");
        }
    }

    private void redraw() {
        ArrayList<PositionImpl> options = new ArrayList<>();
        if (playerColor != null && playerColor.equals(ChessGame.TeamColor.BLACK)) {
            printBoardBlack(currBoard, options);
        } else {
            printBoardWhite(currBoard, options);
        }
        System.out.println("\n");
    }

    private void leave() throws Exception{
        server.leave(authToken, currGameID);
        state = State.LOGGED_IN;
    }

    private void makeMove(String[] args) throws Exception{
        ChessPiece.PieceType promotionType = null;
        if(args.length > 3){
            promotionType = ChessPiece.PieceType.valueOf(args[4]);
        }
        ChessMove move = new MoveImpl(new PositionImpl(args[1]), new PositionImpl(args[2]), promotionType);
        server.makeMove(authToken, currGameID, move);
    }

    private void getMoves(String[] args) {
        PositionImpl position = new PositionImpl(args[1]);
        //check if a piece exits at position
        if(currBoard.getPiece(position) != null){
            //check valid moves
            Collection<ChessMove> validMoves = currGame.validMoves(position);
            ArrayList<PositionImpl> possibleEndPositions = new ArrayList<>();
            for(ChessMove m: validMoves){
                possibleEndPositions.add((PositionImpl) m.getEndPosition());
            }
            if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
                printBoardBlack(currBoard, possibleEndPositions);
            } else {
                printBoardWhite(currBoard, possibleEndPositions);
            }
        }
        else {
            System.out.println("No piece exits at " + position.getColumn() + position.getRow());
        }
    }

    private void resign() throws Exception{
        server.resign(authToken, currGameID);
        state = State.LOGGED_IN;
    }

    private void register(String[] args) throws Exception{
        String username = args[1];
        String password = args[2];
        String email = args[3];
        RegisterResponse response = server.register(new RegisterRequest(username, password, email));
        int status = response.statusCode.code;
        if(status == 200){
            state = State.LOGGED_IN;
            authToken = response.authToken.authToken;
        }
        else if(status == 400 || status == 403 || status == 500){
            System.out.println(response.errorMessage);
        }
    }

    private void login(String[] args) throws Exception{
        String username = args[1];
        String password = args[2];
        LoginResponse response = server.login(new LoginRequest(username, password));
        int status = response.statusCode.code;
        if(status == 200){
            state = State.LOGGED_IN;
            authToken = response.authToken.authToken;
        }
        else if(status == 401 || status == 500){
            System.out.println(response.errorMessage);
        }
    }

    private void logout() throws Exception{
        LogoutResponse response = server.logout(new LogoutRequest(authToken));
        int status = response.statusCode.code;
        if(status == 200){
            state = State.LOGGED_OUT;
            authToken = null;
        }
        else if(status == 401 || status == 500){
            System.out.println(response.errorMessage);
        }
    }
    private void create(String[] args) throws Exception{
        CreateGameResponse response = server.createGame(new CreateGameRequest(authToken, args[1]));
        int status = response.statusCode.code;
        if(status == 200){
            System.out.printf("gameID: " + response.gameID + "%n");
        }
        else if(status == 400 || status == 401 || status == 500){
            System.out.println(response.errorMessage);
        }
    }

    private void list() throws Exception{
        ListGamesResponse response = server.listGames(new ListGamesRequest(authToken));
        int status = response.statusCode.code;
        if(status == 200){
            for(Game game: response.games){
                System.out.printf("gameID: " + game.gameID + "%n");
                if(game.complete){
                    System.out.println("*GAME COMPLETE*");
                }
                else {
                    System.out.println("*GAME IN SESSION*");
                }
                if(game.whiteUsername == null){
                    System.out.printf("white player: AVAILABLE%n");
                }
                else{
                    System.out.printf("white player: " + game.whiteUsername + "%n");
                }
                if(game.blackUsername == null){
                    System.out.printf("black player: AVAILABLE%n");
                }
                else{
                    System.out.printf("black player: " + game.blackUsername + "%n");
                }
                System.out.printf("game name: " + game.gameName + "%n\n");
            }
        }
        else if(status == 401 || status == 500){
            System.out.println(response.errorMessage);
        }
    }

    private void join(String[] args) throws Exception{
        //get player color
        ChessGame.TeamColor color = null;
        if(args.length > 2){
            if(args[2].equals("BLACK")){
                color = ChessGame.TeamColor.BLACK;
            }
            else if(args[2].equals("WHITE")){
                color = ChessGame.TeamColor.WHITE;
            }
        }
        //get game id
        int gameID = -1;
        try{
            gameID = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e){
            System.out.println("Invalid integer input");
        }
        //make request + get response
        JoinGameResponse response = server.joinGame(new JoinGameRequest(authToken, color, gameID));
        int status = response.statusCode.code;
        if(status == 200){
            if(color != null){
                System.out.println("Game: " + gameID + " joined as " + color);
                state = State.IN_GAME;
                playerColor = color;
                currGameID = gameID;
            }
            else{
                System.out.println(("Observing Game: " + gameID));
                state = State.OBSERVING;
                playerColor = null;
                currGameID = gameID;
            }
        }
        else if(status == 400 || status == 401 || status == 403 || status == 500){
            System.out.println(response.errorMessage);
        }
    }

    private void printBoardBlack(ChessBoard board, ArrayList<PositionImpl> options){
        int boardSize = board.getBoardSize();
        PositionImpl currPosition;

        System.out.print("\u001b[100;30;1m    h  g  f  e  d  c  b  a    \u001b[0m\n");

        for(int i = 1; i <= boardSize; i++){
            System.out.print("\u001b[100;30;1m " + i + " ");
            for(int j = boardSize; j >= 1; j--){
                currPosition = new PositionImpl(i, j);
                if((i + j) % 2 == 1){ //white square
                    if(options.contains(currPosition)){
                        System.out.print("\u001b[102m");
                    }
                    else{
                        System.out.print("\u001b[107m");
                    }
                }
                else{ //black square
                    if(options.contains(currPosition)){
                        System.out.print("\u001b[42m");
                    }
                    else{
                        System.out.print("\u001b[0m");
                    }
                }
                PositionImpl position = new PositionImpl(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null){
                    ChessGame.TeamColor color = piece.getTeamColor();
                    if(color.equals(ChessGame.TeamColor.WHITE)){
                        System.out.print("\u001b[34m");
                        switch(piece.getPieceType()){ //white
                            case KING:
                                System.out.print(" K ");
                                break;
                            case QUEEN:
                                System.out.print(" Q ");
                                break;
                            case ROOK:
                                System.out.print(" R ");
                                break;
                            case KNIGHT:
                                System.out.print(" N ");
                                break;
                            case BISHOP:
                                System.out.print(" B ");
                                break;
                            case PAWN:
                                System.out.print(" P ");
                                break;
                        }
                    }
                    else{
                        System.out.print("\u001b[31m");
                        switch(piece.getPieceType()){ //black
                            case KING:
                                System.out.print(" k ");
                                break;
                            case QUEEN:
                                System.out.print(" q ");
                                break;
                            case ROOK:
                                System.out.print(" r ");
                                break;
                            case KNIGHT:
                                System.out.print(" n ");
                                break;
                            case BISHOP:
                                System.out.print(" b ");
                                break;
                            case PAWN:
                                System.out.print(" p ");
                                break;
                        }
                    }
                }
                else{ //empty position
                    System.out.print("   ");
                }
            }
            System.out.print("\u001b[100;30;1m " + i + " ");
            System.out.print("\u001b[0m\n");
        }
        System.out.print("\u001b[100;30;1m    h  g  f  e  d  c  b  a    \u001b[0m\n");
    }



    private void printBoardWhite(ChessBoard board, ArrayList<PositionImpl> options){
        int boardSize = board.getBoardSize();
        PositionImpl currPosition;

        System.out.print("\u001b[100;30;1m    a  b  c  d  e  f  g  h    \u001b[0m\n");

        for(int i = boardSize; i >= 1; i--){
            System.out.print("\u001b[100;30;1m " + i + " ");
            for(int j = 1; j <= boardSize; j++){
                currPosition = new PositionImpl(i, j);
                if((i + j) % 2 == 1){ //white square
                    if(options.contains(currPosition)){
                        System.out.print("\u001b[102m");
                    }
                    else {
                        System.out.print("\u001b[107m");
                    }
                }
                else{ //black square
                    if(options.contains(currPosition)){
                        System.out.print("\u001b[42m");
                    }
                    else{
                        System.out.print("\u001b[0m");
                    }
                }
                PositionImpl position = new PositionImpl(i, j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null){
                    ChessGame.TeamColor color = piece.getTeamColor();
                    if(color.equals(ChessGame.TeamColor.WHITE)){
                        System.out.print("\u001b[34m");
                        switch(piece.getPieceType()){ //white
                            case KING:
                                System.out.print(" K ");
                                break;
                            case QUEEN:
                                System.out.print(" Q ");
                                break;
                            case ROOK:
                                System.out.print(" R ");
                                break;
                            case KNIGHT:
                                System.out.print(" N ");
                                break;
                            case BISHOP:
                                System.out.print(" B ");
                                break;
                            case PAWN:
                                System.out.print(" P ");
                                break;
                        }
                    }
                    else{
                        System.out.print("\u001b[31m");
                        switch(piece.getPieceType()){ //black
                            case KING:
                                System.out.print(" k ");
                                break;
                            case QUEEN:
                                System.out.print(" q ");
                                break;
                            case ROOK:
                                System.out.print(" r ");
                                break;
                            case KNIGHT:
                                System.out.print(" n ");
                                break;
                            case BISHOP:
                                System.out.print(" b ");
                                break;
                            case PAWN:
                                System.out.print(" p ");
                                break;
                        }
                    }
                }
                else{ //empty position
                    System.out.print("   ");
                }
            }
            System.out.print("\u001b[100;30;1m " + i + " ");
            System.out.print("\u001b[0m\n");
        }
        System.out.print("\u001b[100;30;1m    a  b  c  d  e  f  g  h    \u001b[0m\n");
    }

    @Override
    public void onServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                currGame = ((LoadGameMessage) message).game.game;
                currBoard = currGame.getBoard();
                System.out.println("\n");
                redraw();
            }
            case ERROR -> System.out.println(((ErrorMessage) message).errorMessage);
            case NOTIFICATION -> System.out.println(((NotificationMessage) message).message);
        }
        System.out.printf("[" + state + "] >>> ");
    }
}
