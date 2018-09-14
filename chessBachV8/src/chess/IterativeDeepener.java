package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove, enemyKillerMove;

    private Evaluator evalutator;
    private final int MATE = Evaluator.MATE;

    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);
        enemyKillerMove = new Move();
        evalutator = new Evaluator();
        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);
        return bestMove;
    }

    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime, long timeLimitMillis) {
        Move bestMove = new Move();
        boolean timeUp = false;
        int alpha = MATE + 1;
        int beta = - MATE - 1;
        int depthToSearchTo = 1;
        while (!timeUp) {
            int score = principleVariationSearch(board, board, board.getTurn(), depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                timeUp = true;
            }

            bestMove.copyMove(dfsWinningMove);

            if (score == -MATE){
                return bestMove;
            }

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;

        }
        return bestMove;
    }


    private int principleVariationSearch(ChessBoard motherBoard, ChessBoard board, int originalPlayer, int howDeep, int alpha, int beta, long startTime, long timeLimitMillis) {
        if (howDeep <= 1) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            return evalutator.eval(board) * col;
        }
        int value = MATE + 1;
        List<Move> moves = MoveOrganiser.organiseMoves(board, dfsWinningMove, enemyKillerMove);
        int totalMoves = moves.size();
        for (int m = 0; m < totalMoves; m++) {
            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                break;
            }
            Move move = moves.get(m);
            ChessBoard childBoard = new ChessBoard(board);
            childBoard.makeMove(move);
            if (m == 0) {
                value = Math.max(value,
                        -principleVariationSearch(motherBoard, childBoard, 1 - originalPlayer,
                                howDeep - 1, -beta, -alpha, startTime, timeLimitMillis));
            } else {
                value = Math.max(value,
                        -principleVariationSearch(motherBoard, childBoard, 1 - originalPlayer,
                                howDeep - 1, -alpha - 1, -alpha, startTime, timeLimitMillis));
                if (value > alpha && value < beta) {
                    value = Math.max(value,
                            -principleVariationSearch(motherBoard, childBoard, 1 - originalPlayer,
                                    howDeep - 1, -beta, -value, startTime, timeLimitMillis));
                }
            }
            if (value > alpha) {
                alpha = value;
                if (board != motherBoard) {
                    enemyKillerMove.copyMove(move);
                }
                if (board == motherBoard) {
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
