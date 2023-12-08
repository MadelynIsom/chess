package chess;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Objects;

public class PositionImpl implements ChessPosition{
    Integer row;
    Integer column;


    public PositionImpl(Integer row, Integer column){
        this.row = row;
        this.column = column;
    }

    public PositionImpl(String position){
        this.row = Integer.parseInt(String.valueOf(position.charAt(1)));
        this.column = ((position.charAt(0)) - 'a') + 1;
    }

    public static class ChessPositionAdapter implements JsonDeserializer<ChessPosition> {
        public ChessPosition deserialize(JsonElement el, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            return ctx.deserialize(el, PositionImpl.class);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PositionImpl position = (PositionImpl) o;
        return Objects.equals(row, position.row) && Objects.equals(column, position.column);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }
}
