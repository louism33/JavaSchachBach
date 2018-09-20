package chess;

import java.util.List;

public class IterativeDeepener {

    private static int maxDepthReached;
    private static Move dfsWinningMove, enemyKillerMove;

    private static final int MATE = Evaluator.MATE;


//    public static void main (String[] args){
//        IterativeDeepener i = new IterativeDeepener();
//    }
//
//    IterativeDeepener(){
//        ChessBoard board = new ChessBoard();
//        long st = System.currentTimeMillis();
//
//        Move m = expandBoard(board, st, 1000000);
//
//        long et = System.currentTimeMillis();
//        System.out.println("Move we will play: " + m);
//        System.out.println("The whole process took: " + (et - st) + " millis. "+((et-st)/1000)+ " seconds.");
//        System.out.println("Number of normal evals: "+QuiescenceSearch.getNumberOfRegularEvals());
//        System.out.println("Number of quiescent evals: "+QuiescenceSearch.getNumberOfQuiescentEvals());
//
//        System.out.println("That is " + (1000 *
//                (QuiescenceSearch.getNumberOfRegularEvals() +
//                        QuiescenceSearch.getNumberOfQuiescentEvals()) / (et-st))
//                + " calls per second.");
//    }

    static Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);
        return bestMove;
    }


    private static Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime,
                                               long timeLimitMillis) {
        Move bestMove = new Move();
        boolean timeUp = false;
        int depthToSearchTo = 1;
        int score, previousScore = 0;
        while (!timeUp && depthToSearchTo <= 7) {

            score = AspirationSearch.aspirationSearch(board, depthToSearchTo, previousScore,
                    startTime, timeLimitMillis);

            previousScore = score;

            dfsWinningMove = AspirationSearch.getDfsWinningMove();
            enemyKillerMove = AspirationSearch.getEnemyKillerMove();

            bestMove.copyMove(dfsWinningMove);

            boolean debug = true;
            if (debug) {
                System.out.println("--- " + depthToSearchTo);
                System.out.println("DFS: " + dfsWinningMove + " " + score);
                System.out.println("EKM: " + enemyKillerMove);
            }

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                timeUp = true;
            }

            if (score == -MATE){
                return bestMove;
            }

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;

        }
        return bestMove;
    }

    static int getMaxDepthReached() {
        return maxDepthReached;
    }
}
