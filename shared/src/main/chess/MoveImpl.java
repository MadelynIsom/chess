package chess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class MoveImpl implements ChessMove{

    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    public MoveImpl(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public static class ChessMoveAdapter implements JsonDeserializer<ChessMove> {
        public ChessMove deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            return ctx.deserialize(el, MoveImpl.class);
        }
    }
    @Override
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public int hashCode(){
        int rankS = startPosition.getRow();
        int fileS = startPosition.getColumn();
        int rankE = endPosition.getRow();
        int fileE = endPosition.getColumn();

        int typeCode = 1;
        if(promotionPiece != null){
            switch(promotionPiece){
                case KING -> typeCode = 2;
                case QUEEN -> typeCode = 3;
                case BISHOP -> typeCode = 4;
                case KNIGHT -> typeCode = 5;
                case ROOK -> typeCode = 6;
                case PAWN -> typeCode = 7;
            }
        }
        return ((rankS*43) + (fileS *29) + (rankE*4000) + (fileE*499)) * typeCode;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof MoveImpl foreignMove){
            return foreignMove.hashCode() == this.hashCode();
        }
        return false;
    }
}
