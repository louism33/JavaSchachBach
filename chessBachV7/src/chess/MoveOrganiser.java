package chess;

import java.util.ArrayList;
import java.util.List;

public class MoveOrganiser {

    static List<Move> organiseMoves (ChessBoard board, Move dfsWinningMove){

        List<Move> unrankedMoves = board.generateMoves();
        List<Move> moves;
        if (containsMove(unrankedMoves, dfsWinningMove)){
            moves = rankMoves(unrankedMoves, dfsWinningMove);
        }
        else {
            moves = rankMoves(unrankedMoves, null);
        }

        return moves;
    }


    private static boolean containsMove (List<Move> unrankedMoves, Move m){
        for (Move move : unrankedMoves){
            if(move.equals(m)) return true;
        }
        return false;
    }

    private static List<Move> rankMoves (List<Move> moves, Move killerMove) {
        List<Move> rankedMoves = new ArrayList<>(moves.size());
        List<Move> temp = new ArrayList<>();

        if (killerMove != null){
            rankedMoves.add(killerMove);
        }
        for (Move move : moves){
            if (killerMove != null) {
                if (move.equals(killerMove)) {
                    continue;
                }
            }
            if (move.capture){
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5 && move.desty >= 2 && move.desty <= 5){ //destination is centre of board
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5){
                rankedMoves.add(move);
                continue;
            }
            if (move.desty >= 2 && move.desty <= 5){
                rankedMoves.add(move);
                continue;
            }
            temp.add(move);
        }
        rankedMoves.addAll(temp);
        return rankedMoves;
    }
}
