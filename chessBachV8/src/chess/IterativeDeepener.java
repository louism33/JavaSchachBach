package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove, enemyKillerMove;

    private final int MATE = Evaluator.MATE;

    // TODO: (Null Move) Pruning

    public static void main (String[] args){
        IterativeDeepener i = new IterativeDeepener();
    }

    IterativeDeepener(){
        ChessBoard board = new ChessBoard();
        long st = System.currentTimeMillis();

        Move m = expandBoard(board, st, 100);
        System.out.println(m);
    }

    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);
        enemyKillerMove = new Move();
        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);
        return bestMove;
    }




    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime,
                                               long timeLimitMillis) {
        Move bestMove = new Move();
        boolean timeUp = false;
        int depthToSearchTo = 1;
        int score, previousScore = 0;
        while (!timeUp) {

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

    int getMaxDepthReached() {
        return maxDepthReached;
    }
}
