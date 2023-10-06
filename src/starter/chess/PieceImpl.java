package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceImpl implements ChessPiece{

    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;

    public PieceImpl(ChessPiece.PieceType type, ChessGame.TeamColor color){
        this.type = type;
        this.color = color;
    }
    @Override
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    @Override
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //use switch to check piece type to handle each type of moves
        switch(type){
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case PAWN:
                return pawnMoves(board, myPosition);
        }
        return null;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rank = myPosition.getRow();
        int file = myPosition.getColumn();
        ChessPosition tempPosition1;
        ChessPosition tempPosition2;

        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            if(rank == 2){ //if white and in row 2, check if both spaces ahead are unoccupied
                tempPosition1 = new PositionImpl(rank+1, file);
                tempPosition2 = new PositionImpl(rank+2, file);
                if(board.getPiece(tempPosition1) == null && board.getPiece(tempPosition2) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition2);
                }
            }
            if(rank == 7){ //handle promotion
                //if diagonal forward right is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file+1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }
                //if diagonal forward left is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file-1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }
                //if forward space is unoccupied, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file);
                if(board.getPiece(tempPosition1) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }

            }
            else{
                //if diagonal forward right is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file+1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
                //if diagonal forward left is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file-1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
                //if forward space is unoccupied, add to suggested
                tempPosition1 = new PositionImpl(rank+1, file);
                if(board.getPiece(tempPosition1) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
            }
        }
        else{ // if black...
            tempPosition1 = new PositionImpl(rank-1, file);
            tempPosition2 = new PositionImpl(rank-2, file);
            if(rank == 7){ //if black and in row 7 and both spaces ahead are unoccupied, add rank -2 to suggested
                if(board.getPiece(tempPosition1) == null && board.getPiece(tempPosition2) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition2);
                }
            }
            if(rank == 2){ //handle promotion
                //if diagonal forward right is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file+1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }
                //if diagonal forward left is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file-1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }
                //if forward space is unoccupied, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file);
                if(board.getPiece(tempPosition1) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.QUEEN);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.KNIGHT);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.BISHOP);
                    addToSuggested(possibleMoves, myPosition, tempPosition1, PieceType.ROOK);
                }
            }
            else{
                //if diagonal forward right is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file+1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
                //if diagonal forward left is occupied by enemy, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file-1);
                if(isOnBoard(board, tempPosition1) && (isOccupiedByEnemy(board, tempPosition1))){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
                //if forward space is unoccupied, add to suggested
                tempPosition1 = new PositionImpl(rank-1, file);
                if(board.getPiece(tempPosition1) == null){
                    addToSuggested(possibleMoves, myPosition, tempPosition1);
                }
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rank = myPosition.getRow();
        int file = myPosition.getColumn();
        ChessPosition tempPosition;
        //loop through ranks on board forward from current position
        if((rank +1) <= board.getBoardSize()){ //check that initial modification is a valid position (not off the board)
            for(int i = rank +1; i <= board.getBoardSize(); i++){
                tempPosition = new PositionImpl(i, file);
                if(isOccupiedByTeam(board, tempPosition)){ //if blocked by team piece break from loop on that leg
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){ //if blocked by enemy piece, add to possible and break loop on that leg
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition); // if space is not occupied, add to suggested and continue
            }
        }
        //loop through ranks on board backward from current position
        if((rank -1) >= 1){
            for(int i = rank -1; i >= 1; i--){
                tempPosition = new PositionImpl(i, file);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }
        //loop through files on board right from current position
        if((file +1) <= board.getBoardSize()){
            for(int i = file +1; i <= board.getBoardSize(); i++){
                tempPosition = new PositionImpl(rank, i);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }
        //loop through files on board left from current position
        if((file -1) >= 1){
            for(int i = file -1; i >= 1; i--){
                tempPosition = new PositionImpl(rank, i);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rank = myPosition.getRow();
        int file = myPosition.getColumn();
        ChessPosition tempPosition;
        //forward 1 right 2
        tempPosition = new PositionImpl(rank +1, file +2);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //forward 1 left 2
        tempPosition = new PositionImpl(rank +1, file -2);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //backward 1 right 2
        tempPosition = new PositionImpl(rank -1, file +2);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //backward 1 left 2
        tempPosition = new PositionImpl(rank -1, file -2);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //forward 2 right 1
        tempPosition = new PositionImpl(rank +2, file +1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //forward 2 left 1
        tempPosition = new PositionImpl(rank +2, file -1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //backward 2 right 1
        tempPosition = new PositionImpl(rank -2, file +1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //backward 2 left 1
        tempPosition = new PositionImpl(rank -2, file -1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board, tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }

        return possibleMoves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rank = myPosition.getRow();
        int file = myPosition.getColumn();
        ChessPosition tempPosition;
        //loop through diagonal spaces forward and right from current position
        if((rank +1) <= board.getBoardSize() && (file +1) <= board.getBoardSize()){ //check that initial modification is a valid position
            for(int i = rank +1, j = file +1; i <= board.getBoardSize() && j<= board.getBoardSize(); i++, j++){
                tempPosition = new PositionImpl(i, j);
                if(isOccupiedByTeam(board, tempPosition)){ //if blocked by team piece break from loop on that leg
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){ //if blocked by enemy piece, add to possible and break loop on that leg
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition); // if space is not occupied, add to suggested and continue
            }
        }
        //loop through diagonal spaces backward and right from current position
        if((rank -1) >= 1 && (file +1) <= board.getBoardSize()){
            for(int i = rank -1, j = file +1; i >= 1 && j <= board.getBoardSize(); i--, j++){
                tempPosition = new PositionImpl(i, j);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }
        //loop through diagonal spaces forward and left from current position
        if((rank +1) <= board.getBoardSize() && (file -1) >= 1){
            for(int i = rank +1, j = file -1; i <= board.getBoardSize() && j >= 1; i++, j--){
                tempPosition = new PositionImpl(i, j);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }
        //loop through diagonal spaces backward and left from current position
        if((rank -1) >= 1 && (file -1) >= 1){
            for(int i = rank -1, j = file -1; i >= 1 && j >= 1; i--, j--){
                tempPosition = new PositionImpl(i, j);
                if(isOccupiedByTeam(board, tempPosition)){
                    break;
                }
                if(isOccupiedByEnemy(board, tempPosition)){
                    addToSuggested(possibleMoves, myPosition, tempPosition);
                    break;
                }
                addToSuggested(possibleMoves, myPosition, tempPosition);
            }
        }

        return possibleMoves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> list1 = rookMoves(board, myPosition);
        Collection<ChessMove> list2 = bishopMoves(board, myPosition);
        list1.addAll(list2);
        return list1;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> possibleMoves = new ArrayList<>();
        int rank = myPosition.getRow();
        int file = myPosition.getColumn();
        //right one space
        ChessPosition tempPosition = new PositionImpl(rank, file +1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //diagonal forward right
        tempPosition = new PositionImpl(rank +1, file +1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //advance one space
        tempPosition = new PositionImpl(rank +1, file);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //diagonal forward left
        tempPosition = new PositionImpl(rank +1, file -1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //left one space
        tempPosition = new PositionImpl(rank, file -1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //diagonal backward left
        tempPosition = new PositionImpl(rank -1, file -1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //retreat one space
        tempPosition = new PositionImpl(rank -1, file);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        //diagonal backward right
        tempPosition = new PositionImpl(rank -1, file +1);
        if(isOnBoard(board, tempPosition) && !(isOccupiedByTeam(board,tempPosition))){
            addToSuggested(possibleMoves, myPosition, tempPosition);
        }
        return possibleMoves;
    }

    private boolean isOccupiedByEnemy(ChessBoard board, ChessPosition reqPosition){
        boolean isOccupiedByEnemy = false;
        if(board.getPiece(reqPosition) == null){
            return isOccupiedByEnemy;
        }
        if(board.getPiece(reqPosition).getTeamColor() != this.getTeamColor()){
            isOccupiedByEnemy = true; //check if position is occupied by enemy piece
        }
        return isOccupiedByEnemy;
    }

    private boolean isOccupiedByTeam(ChessBoard board, ChessPosition reqPosition){
        boolean isOccupiedByTeam = false;
        if(board.getPiece(reqPosition) == null){
            return isOccupiedByTeam; //false
        }
        if(board.getPiece(reqPosition).getTeamColor() == this.getTeamColor()){
            isOccupiedByTeam = true; //check if position is occupied by team piece
        }
        return isOccupiedByTeam;
    }


    private boolean isOnBoard(ChessBoard board, ChessPosition reqPosition){
        boolean isOnBoard = true;
        if(reqPosition.getRow() > board.getBoardSize() || reqPosition.getColumn() > board.getBoardSize() || reqPosition.getRow() < 1 || reqPosition.getColumn() < 1){
            isOnBoard = false;
        }
        return isOnBoard;
    }

    private void addToSuggested(ArrayList<ChessMove> possibleMoves, ChessPosition currPosition, ChessPosition reqPosition){
        MoveImpl newMove = new MoveImpl(currPosition, reqPosition, null);
        possibleMoves.add(newMove);
    }

    private void addToSuggested(ArrayList<ChessMove> possibleMoves, ChessPosition currPosition, ChessPosition reqPosition, ChessPiece.PieceType promotionPiece){
        MoveImpl newMove = new MoveImpl(currPosition, reqPosition, promotionPiece);
        possibleMoves.add(newMove);
    }
/*
    @Override
    public int hashCode(){
        int colorCode;
        int typeCode = 0;
        if(getTeamColor() == ChessGame.TeamColor.WHITE){
            colorCode = 71;
        }
        else{
            colorCode = 37;
        }
        switch(type){
            case KING -> typeCode = 1;
            case QUEEN -> typeCode = 2;
            case BISHOP -> typeCode = 3;
            case KNIGHT -> typeCode = 4;
            case ROOK -> typeCode = 5;
            case PAWN -> typeCode = 6;
        }
        return colorCode * typeCode;
    }

 */
}
