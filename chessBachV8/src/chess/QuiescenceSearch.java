package chess;

import java.util.ArrayList;
import java.util.List;

public class QuiescenceSearch {

    static int quiescentSearchOrganiser (ChessBoard board, int originalPlayer,
                                         int alpha, int beta,
                                         long startTime, long timeLimitMillis) {

        return quietNode(board)
                ? Evaluator.eval(board)
                : quiescentSearch(board, originalPlayer, alpha, beta, startTime, timeLimitMillis);
    }

    private static int quiescentSearch(ChessBoard board, int originalPlayer, int alpha, int beta,
                                long startTime, long timeLimitMillis){


        int score = Evaluator.eval(board);

        if (score >= beta) return beta;
        if (score > alpha) {
            alpha = score;
        }

        List<Move> allDangerousMoves = allCaptureMoves(board);
        allDangerousMoves.addAll(allCheckMoves(board));
        int totalDangerousMoves = allDangerousMoves.size();

//        System.out.println("DangerousMoves: "+ allDangerousMoves +" player: " + board.getTurn());
        for (int m = 0; m < totalDangerousMoves; m++) {

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                break;
            }


            Move move = allDangerousMoves.get(m);

            ChessBoard childBoard = new ChessBoard(board);
            childBoard.makeMove(move);


            int value = -quiescentSearch(childBoard, 1 - originalPlayer,
                    -beta, -alpha, startTime, timeLimitMillis);

            if (value >= beta) return beta;
            if (value > alpha) {
                alpha = value;
            }
        }
        return alpha;
    }


    private static int numberOfCaptureMoves (ChessBoard board){
        return allCaptureMoves(board).size();
    }

    private static List<Move> allCaptureMoves (ChessBoard board){
        List<Move> allMoves = board.generateMoves();
        List<Move> allCaptureMoves = new ArrayList<>();
        for (Move move : allMoves){
            if (move.capture){
                allCaptureMoves.add(move);
            }
        }
        return allCaptureMoves;
    }

    private static List<Move> allCheckMoves(ChessBoard board){
        List<Move> allMoves = board.generateMoves();
        List<Move> allCheckMoves = new ArrayList<>();

        for (Move move : allMoves){
            ChessBoard b = new ChessBoard(board);
            b.makeMove(move);
            if (b.inCheck()){
                allCheckMoves.add(move);
            }
        }
        return allCheckMoves;
    }

    private static boolean quietNode (ChessBoard board){
        if (board.inCheck()) return false;
        if (numberOfCaptureMoves(board) > 0) return false;
        if (allCheckMoves(board).size() > 0) return false;
        return true;
    }

}
