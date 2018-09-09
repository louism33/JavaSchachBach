package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove;

    private int MATE = Evaluator.MATE;

    private Evaluator evalutator;

    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);

        evalutator = new Evaluator();

        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);

        return bestMove;
    }

    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime, long timeLimitMillis) {
        Move bestMove = new Move();
        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depthToSearchTo = 1;

        while (!timeUp && depthToSearchTo <= 10) {

            long currentTime = System.currentTimeMillis();

            long timeLeft = startTime + timeLimitMillis - currentTime;

            if (timeLeft < 0) {
                timeUp = true;
            }

            int player = board.getTurn();

            int score = principleVarSearch(board, board, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);
            bestMove.copyMove(dfsWinningMove);

            if (score == Integer.MAX_VALUE || score == MATE){
                return bestMove;
            }

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;

        }
        return bestMove;
    }


    private int principleVarSearch (ChessBoard motherBoard, ChessBoard board,
                         int originalPlayer, int howDeep, int alpha, int beta,
                         long startTime, long timeLimitMillis) {

        int value;

        if (howDeep <= 1) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            return evalutator.eval(board) * col;
        }

        List<Move> moves = MoveOrganiser.organiseMoves(board, dfsWinningMove);

        int len = moves.size();
        for (int m = 0; m < len; m++) {

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                break;
            }

            Move move = moves.get(m);
            ChessBoard childBoard = new ChessBoard(board);
            childBoard.makeMove(move);

            if (m == 0) {
                value = (-1) * principleVarSearch(motherBoard, childBoard, 1 - originalPlayer,
                        howDeep - 1, -beta, -alpha, startTime, timeLimitMillis);
            } else {
                value = (-1) * principleVarSearch(motherBoard, childBoard, 1 - originalPlayer,
                        howDeep - 1, -alpha - 1, -alpha, startTime, timeLimitMillis);

                if (alpha < value && value < beta) {
                    value = (-1) * principleVarSearch(motherBoard, childBoard, 1 - originalPlayer,
                            howDeep - 1, -beta, -value, startTime, timeLimitMillis);
                }
            }
            if (value > alpha) {
                alpha = value;
                if (board == motherBoard){
                    dfsWinningMove.copyMove(move);
                }
            }
            if (alpha >= beta) {
                break;
            }
        }
        return alpha;
    }

    int getMaxDepthReached() {
        return maxDepthReached;
    }
}
