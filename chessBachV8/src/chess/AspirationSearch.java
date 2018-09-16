package chess;

public class AspirationSearch {

    private static final int MATE = Evaluator.MATE;
    private static Move dfsWinningMove = new Move(), enemyKillerMove = new Move();



    static int aspirationSearch(ChessBoard board, int depthToSearchTo, int aspirationScore,
                                 long startTime, long timeLimitMillis){

        int firstWindow = 50, windowDelta = firstWindow * 2;
        int alpha, beta;
        int score = 0;
        boolean timeUp = false;

        if (depthToSearchTo > 1){
            alpha = aspirationScore - firstWindow;
            beta = aspirationScore + firstWindow;
        } else {
            alpha = MATE + 1;
            beta = - MATE - 1;
        }

        while (!timeUp){
            score = PrincipleVariationSearch.principleVariationSearch(board, board, board.getTurn(),
                    depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            dfsWinningMove = PrincipleVariationSearch.getDfsWinningMove();
            enemyKillerMove = PrincipleVariationSearch.getEnemyKillerMove();

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                timeUp = true;
            }

            if (score <= alpha){
                alpha = alpha - windowDelta;
            }
            else if (score >= beta){
                beta = beta + windowDelta;
            }
            else {
                break;
            }
        }


        return score;
    }


    public static Move getDfsWinningMove() {
        return dfsWinningMove;
    }

    public static Move getEnemyKillerMove() {
        return enemyKillerMove;
    }
}
