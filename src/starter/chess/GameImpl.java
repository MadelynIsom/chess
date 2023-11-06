package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GameImpl implements ChessGame{
    ChessBoard board;
    TeamColor teamTurn;
    boolean isWhiteTurn;


    public GameImpl(){
        board = new BoardImpl();
        board.resetBoard();
        isWhiteTurn = true;
        teamTurn = TeamColor.WHITE;
    }
    @Override
    public TeamColor getTeamTurn() {
        if(isWhiteTurn){
            return TeamColor.WHITE;
        }
        else return TeamColor.BLACK;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        if(getTeamTurn() == TeamColor.WHITE){
           teamTurn = TeamColor.WHITE;
        }
        else teamTurn = TeamColor.BLACK;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(board.getPiece(startPosition) != null){
            Set<ChessMove> validMoves = new HashSet<>();
            Set<ChessMove> possibleMoves = new HashSet<>(board.getPiece(startPosition).pieceMoves(board, startPosition));
            for(ChessMove move: possibleMoves){
                if(!leavesKingInCheck(move)){
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
        return null;
    }

    private boolean leavesKingInCheck(ChessMove move){
        ChessBoard tempBoard = new BoardImpl(board);
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece piece = tempBoard.getPiece(startPosition);
        if(promotionPiece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 8 || endPosition.getRow() == 1)){
            ChessPiece newPromotionPiece = new PieceImpl(promotionPiece, piece.getTeamColor());
            tempBoard.removePiece(startPosition, piece);
            tempBoard.addPiece(endPosition, newPromotionPiece);
        }
        else{
            tempBoard.removePiece(startPosition, piece);
            tempBoard.addPiece(endPosition, piece);
        }

        ChessPosition currPosition;
        ChessPiece currPiece;
        TeamColor teamColor = piece.getTeamColor();
        ChessPosition kingPosition = null;
        Set<ChessMove> enemyMoves = new HashSet<>();

        for(int i = 1; i <= tempBoard.getBoardSize(); i++){
            for(int j = 1; j <= tempBoard.getBoardSize(); j++){
                currPosition = new PositionImpl(i, j);
                if(tempBoard.getPiece(currPosition) != null){
                    currPiece = new PieceImpl(tempBoard.getPiece(currPosition).getPieceType(), tempBoard.getPiece(currPosition).getTeamColor());
                    if(currPiece.getTeamColor() != teamColor){ //get enemy's possible moves
                        Set<ChessMove> currPieceMoves = new HashSet<>(currPiece.pieceMoves(tempBoard, currPosition));
                        enemyMoves.addAll(currPieceMoves);
                    }
                    if(currPiece.getPieceType() == ChessPiece.PieceType.KING && currPiece.getTeamColor() == teamColor){
                        kingPosition = currPosition;
                    }
                }
            }
        }

        for(ChessMove currMove: enemyMoves){
            if(currMove.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;

    }

    private boolean hasValidMoves(ChessPosition position){
        ChessPiece piece = board.getPiece(position);
        Set<ChessMove> pieceMoves = new HashSet<>(piece.pieceMoves(board, position));
        //check all valid moves
        Set<ChessMove> validMoves = new HashSet<>();
        for(ChessMove move: pieceMoves){
            Collection<ChessMove> temp = validMoves(move.getStartPosition());
            if(temp != null){
                validMoves.addAll(validMoves(move.getStartPosition()));
            }
        }
        if(validMoves.isEmpty()){
            return false;
        }
        else return true;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = validMoves(startPosition);
        if(getTeamTurn() == piece.getTeamColor() && validMoves.contains(move)){ //if it is the piece's team's turn & move is valid...
            if(promotionPiece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPosition.getRow() == 8 || endPosition.getRow() == 1)){
                ChessPiece newPromotionPiece = new PieceImpl(promotionPiece, piece.getTeamColor());
                board.removePiece(startPosition, piece);
                board.addPiece(endPosition, newPromotionPiece);
            }
            else{
                board.removePiece(startPosition, piece);
                board.addPiece(endPosition, piece);
            }
            isWhiteTurn = !isWhiteTurn;
        }
        else{
            throw new InvalidMoveException("Invalid Move!");
        }
        //if the piece can not move where requested
        //or the move leaves the king in danger
        //or it is not the pieces turn

    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition currPosition;
        ChessPiece currPiece;
        ChessPosition kingPosition = null;
        Set<ChessMove> enemyMoves = new HashSet<>();
        //loop though each position in board to find the team's king & create collection of possible enemy moves
        for(int i = 1; i <= board.getBoardSize(); i++){
            for(int j = 1; j <= board.getBoardSize(); j++){
                currPosition = new PositionImpl(i, j);
                if(board.getPiece(currPosition) != null){
                    currPiece = new PieceImpl(board.getPiece(currPosition).getPieceType(), board.getPiece(currPosition).getTeamColor());
                    if(currPiece.getTeamColor() != teamColor){ //get enemy's possible moves
                        Set<ChessMove> currPieceMoves = new HashSet<>(currPiece.pieceMoves(board, currPosition));
                        enemyMoves.addAll(currPieceMoves);
                    }
                    if(currPiece.getPieceType() == ChessPiece.PieceType.KING && currPiece.getTeamColor() == teamColor){
                        kingPosition = currPosition;
                    }
                }
            }
        }
        for(ChessMove currMove: enemyMoves){
            if(currMove.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            ChessPosition currPosition;
            ChessPiece currPiece;
            Set<ChessMove> teamMoves = new HashSet<>();
            //loop though each position in board to find the team's king & create collection of all possible team moves
            for(int i = 1; i <= board.getBoardSize(); i++){
                for(int j = 1; j <= board.getBoardSize(); j++){
                    currPosition = new PositionImpl(i, j);
                    if(board.getPiece(currPosition) != null){
                        currPiece = new PieceImpl(board.getPiece(currPosition).getPieceType(), board.getPiece(currPosition).getTeamColor());
                        if(currPiece.getTeamColor() == teamColor){ //get teams possible moves
                            Set<ChessMove> currPieceMoves = new HashSet<>(currPiece.pieceMoves(board, currPosition));
                            teamMoves.addAll(currPieceMoves);
                        }
                    }
                }
            }
            Set<ChessMove> validMoves = new HashSet<>();
            for(ChessMove move: teamMoves){
                ChessPosition startPosition = move.getStartPosition();
                validMoves.addAll(validMoves(startPosition));
            }
            if(validMoves.isEmpty()){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition currPosition;
        ChessPiece currPiece;
        for(int i = 1; i <= board.getBoardSize(); i++){
            for(int j = 1; j <= board.getBoardSize(); j++){
                currPosition = new PositionImpl(i, j);
                currPiece = board.getPiece(currPosition);
                if(currPiece != null && teamColor == currPiece.getTeamColor() && hasValidMoves(currPosition)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GameImpl foreignGame){
            return (foreignGame.board.equals(board) && foreignGame.teamTurn == teamTurn && foreignGame.isWhiteTurn == isWhiteTurn);
        }
        return false;
    }

}
