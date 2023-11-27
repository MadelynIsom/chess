package chess;

import java.util.Objects;

public class PositionImpl implements ChessPosition{
    Integer row;
    Integer column;


    public PositionImpl(Integer row, Integer column){
        this.row = row;
        this.column = column;
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
/*
    @Override
    public int hashCode(){
        return 29 * row * column;
    }

 */
}
