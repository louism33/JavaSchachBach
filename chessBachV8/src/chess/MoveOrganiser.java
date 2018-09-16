package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoveOrganiser {

    static List<Move> organiseMoves(ChessBoard board, Move dfsWinningMove, Move enemyKillerMove) {
        List<Move> unrankedMoves = board.generateMoves();
        List<Move> moves;

        if (containsMove(unrankedMoves, dfsWinningMove)) {
            moves = rankMoves(unrankedMoves, dfsWinningMove);
        } else if (containsMove(unrankedMoves, enemyKillerMove)){
            moves = rankMoves(unrankedMoves, enemyKillerMove);
        } else {
            moves = rankMoves(unrankedMoves, null);
        }
        return moves;
    }

    static List<Move> organiseMovesForQuiescent(ChessBoard board, List<Move> unrankedMoves){
        if (unrankedMoves.size() < 2) return unrankedMoves;
        Collections.sort(unrankedMoves,
                ((Move a, Move b) -> qScore(board, b) - qScore(board, a))
                );
        return unrankedMoves;
    }

    private static int qScore (ChessBoard board, Move move){
        ChessBoard.SquareDesc takingPiece =
                board.getSquare(move.srcx, move.srcy);
        ChessBoard.SquareDesc victimPiece =
                board.getSquare(move.destx, move.desty);

        if (victimPiece.type == 6) {
            return 0; // checking moves placed in middle
        }

        return qScoreHelper(victimPiece) - qScoreHelper(takingPiece);
    }

    private static int qScoreHelper(ChessBoard.SquareDesc piece){
        switch (piece.type) {
            case 5 : return 1;
            case 0 : return 3;
            case 1 : return 3;
            case 2 : return 5;
            case 3 : return 9;
            default: return 0;
        }
    }


    private static boolean containsMove(List<Move> unrankedMoves, Move m) {
        for (Move move : unrankedMoves) {
            if (move.equals(m)) return true;
        }
        return false;
    }

    private static List<Move> rankMoves(List<Move> moves, Move killerMove) {
        List<Move> rankedMoves = new ArrayList<>(moves.size());
        List<Move> temp = new ArrayList<>();
        List<Move> temp1 = new ArrayList<>();
        List<Move> temp2 = new ArrayList<>();
        if (killerMove != null) {
            rankedMoves.add(killerMove);
        }
        for (Move move : moves) {
            if (killerMove != null) {
                if (move.equals(killerMove)) {
                    continue;
                }
            }
            if (move.capture) {
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 3 && move.destx <= 4 &&
                    move.desty >= 3 && move.desty <= 4) {
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5 && move.desty >= 2 && move.desty <= 5) { //destination is centre of board
                temp2.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5) {
                temp1.add(move);
                continue;
            }
            if (move.desty >= 2 && move.desty <= 5) {
                temp1.add(move);
                continue;
            }
            temp.add(move);
        }
        rankedMoves.addAll(temp2);
        rankedMoves.addAll(temp1);
        rankedMoves.addAll(temp);

        assert moves.size() == rankedMoves.size();

        return rankedMoves;
    }
}