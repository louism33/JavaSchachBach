package chess;

import java.util.List;

class SearchPrincipleVariation {

    private static final int MATE = Evaluator.MATE;
    private static Move dfsWinningMove = new Move(), enemyKillerMove = new Move();

    private static int PVSCounter = 0;

    public static int getPVSCounter() {
        return PVSCounter;
    }

    static int principleVariationSearch(ChessBoard motherBoard, ChessBoard board,
                                        int originalPlayer, int howDeep,
                                        int alpha, int beta, long startTime, long timeLimitMillis) {
        if (howDeep <= 1) {
            return SearchQuiescence.quiescentSearchOrganiser(board, originalPlayer,
                    alpha, beta, startTime, timeLimitMillis);

        }
        int value = MATE + 1;
        List<Move> moves = MoveOrganiser.organiseMoves(board, dfsWinningMove, enemyKillerMove);
        int totalMoves = moves.size();

        boolean timeUp = false;
        for (int m = 0; m < totalMoves; m++) {

            if (!timeUp) {
                PVSCounter++;
                Move move = moves.get(m);
                ChessBoard childBoard = new ChessBoard(board);
                childBoard.makeMove(move);
                if (m == 0) {
                    value = Math.max(value,
                            -principleVariationSearch(motherBoard, childBoard,
                                    1 - originalPlayer, howDeep - 1,
                                    -beta, -alpha, startTime, timeLimitMillis));
                } else {
                    value = Math.max(value,
                            -principleVariationSearch(motherBoard, childBoard,
                                    1 - originalPlayer, howDeep - 1,
                                    -alpha - 1, -alpha, startTime, timeLimitMillis));
                    if (value > alpha && value < beta) {
                        value = Math.max(value,
                                -principleVariationSearch(motherBoard, childBoard,
                                        1 - originalPlayer, howDeep - 1,
                                        -beta, -value, startTime, timeLimitMillis));
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

                long currentTime = System.currentTimeMillis();
                long timeLeft = startTime + timeLimitMillis - currentTime;
                if (timeLeft < 0) {
                    timeUp = true;
                }

            }
        }
        return alpha;
    }

    static Move getDfsWinningMove() {
        return dfsWinningMove;
    }

    static Move getEnemyKillerMove() {
        return enemyKillerMove;
    }
}
