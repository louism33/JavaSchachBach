package chess;

class SearchIterativeDeepener {

    private static int maxDepthReached;
    private static Move dfsWinningMove;

    private static final int MATE = Evaluator.MATE;

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
        while (!timeUp) {

            score = SearchAspiration.aspirationSearch(board, depthToSearchTo, previousScore,
                    startTime, timeLimitMillis);

            previousScore = score;

            dfsWinningMove = SearchAspiration.getDfsWinningMove();

            bestMove.copyMove(dfsWinningMove);

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
}
