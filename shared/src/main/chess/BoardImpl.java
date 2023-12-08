package chess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BoardImpl implements ChessBoard{
    private int boardSize;
    protected ChessPiece[][] boardPositions;

    public BoardImpl(){
        boardSize = 8;
        boardPositions = new ChessPiece[boardSize][boardSize];
    }

    public BoardImpl(ChessBoard board){
        boardSize = 8;
        boardPositions = new ChessPiece[boardSize][boardSize];
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                boardPositions[i][j] = board.getPiece(new PositionImpl(i +1, j +1));
            }
        }
    }

    public static class ChessBoardAdapter implements JsonDeserializer<ChessBoard> {
        public ChessBoard deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            return ctx.deserialize(el, BoardImpl.class);
        }
    }

    public void removePiece(ChessPosition position, ChessPiece piece){
        boardPositions[position.getRow() -1][position.getColumn() -1] = null;
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardPositions[position.getRow() -1][position.getColumn() -1] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return boardPositions[position.getRow() -1][position.getColumn() -1];
    }

    @Override
    public void resetBoard() {
        //clear board
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                boardPositions[i][j] = null;
            }
        }
        //set starting board
        addPiece( new PositionImpl(1,1), new PieceImpl(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,2), new PieceImpl(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,3), new PieceImpl(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,4), new PieceImpl(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,5), new PieceImpl(ChessPiece.PieceType.KING, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,6), new PieceImpl(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,7), new PieceImpl(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(1,8), new PieceImpl(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,1), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,2), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,3), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,4), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,5), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,6), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,7), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));
        addPiece( new PositionImpl(2,8), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.WHITE));

        addPiece( new PositionImpl(8,1), new PieceImpl(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,2), new PieceImpl(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,3), new PieceImpl(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,4), new PieceImpl(ChessPiece.PieceType.QUEEN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,5), new PieceImpl(ChessPiece.PieceType.KING, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,6), new PieceImpl(ChessPiece.PieceType.BISHOP, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,7), new PieceImpl(ChessPiece.PieceType.KNIGHT, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(8,8), new PieceImpl(ChessPiece.PieceType.ROOK, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,1), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,2), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,3), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,4), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,5), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,6), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,7), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
        addPiece( new PositionImpl(7,8), new PieceImpl(ChessPiece.PieceType.PAWN, ChessGame.TeamColor.BLACK));
    }

    @Override
    public int getBoardSize() {
        return boardSize;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoardImpl foreignBoard) {
            if(foreignBoard.boardSize != boardSize){
                return false;
            }
            for(int i = 0; i < boardSize; i++){
                for(int j = 0; j < boardSize; j++){
                    ChessPiece piece = boardPositions[i][j];
                    ChessPiece foreignPiece = foreignBoard.boardPositions[i][j];
                    if(foreignPiece != null && piece != null && !piece.equals(foreignPiece)){
                        return false;
                    }
                    else if((foreignPiece == null && piece != null) || (foreignPiece != null && piece == null) ){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
