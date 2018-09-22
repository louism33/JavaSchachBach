package chess;

import java.util.ArrayList;
import java.util.List;

class SearchIterativeDeepener {

    private static int maxDepthReached;
    private static Move dfsWinningMove;

    private static final int MATE = Evaluator.MATE;

    private static List<String> movesAsStrings (ChessBoard board){
        List<String> movesStrings = new ArrayList<>();
        List<Move> moves = board.generateMoves();
        for (Move move : moves){
            movesStrings.add(move.toString());
        }
        return movesStrings;
    }

    static Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis) {
        List<Move> moves = board.generateMoves();

        if (moves.size() == 0){
            if (board.inCheck()) System.out.println("Checkmate");
            else System.out.println("Stalemate");
        }

        if (moves.size() == 1){
            return moves.get(0);
        }

        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);

        if (movesAsStrings(board).contains(bestMove.toString())){
            return bestMove;
        }

        return moves.get(0);
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
