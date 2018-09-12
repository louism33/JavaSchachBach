package chess;

import java.util.ArrayList;
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

        while (!timeUp) {

            long currentTime = System.currentTimeMillis();

            long timeLeft = startTime + timeLimitMillis - currentTime;

            if (timeLeft < 0) {
                timeUp = true;
            }

            int player = board.getTurn();
            int score = depthFirstSearchBoard(board, board, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            bestMove.copyMove(dfsWinningMove);

            if (score == Integer.MAX_VALUE || score == MATE){
                return bestMove;
            }


            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;

        }
        return bestMove;
    }


    private int depthFirstSearchBoard(ChessBoard motherBoard, ChessBoard board, int originalPlayer, int howDeep, int alpha, int beta, long startTime, long timeLimitMillis) {

        if (howDeep <= 1) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            return evalutator.eval(board) * col;
        }

        if (board.getTurn() == originalPlayer) {
            int value = Integer.MIN_VALUE;
            List<Move> unrankedMoves = board.generateMoves();
            List<Move> moves;
            if (containsMove(unrankedMoves, dfsWinningMove)){
                moves = rankMoves(unrankedMoves, dfsWinningMove);
            }
            else {
                moves = rankMoves(unrankedMoves, null);
            }

            for (Move move : moves) {
                long currentTime = System.currentTimeMillis();
                long timeLeft = startTime + timeLimitMillis - currentTime;
                if (timeLeft < 0) {
                    break;
                }
                ChessBoard childBoard = new ChessBoard(board);
                childBoard.makeMove(move);
                value = Math.max(value,
                        depthFirstSearchBoard(motherBoard, childBoard, originalPlayer, howDeep - 1, alpha, beta, startTime, timeLimitMillis));
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
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            List<Move> unrankedMoves = board.generateMoves();
            List<Move> moves = rankMoves(unrankedMoves, null);
            for (Move move : moves) {
                long currentTime = System.currentTimeMillis();
                long timeLeft = startTime + timeLimitMillis - currentTime;
                if (timeLeft < 0) {
                    break;
                }
                ChessBoard childBoard = new ChessBoard(board);
                childBoard.makeMove(move);
                value = Math.min(value,
                        depthFirstSearchBoard(motherBoard, childBoard, originalPlayer, howDeep - 1, alpha, beta, startTime, timeLimitMillis));

                if (value < beta) {
                    beta = value;
                    if (board == motherBoard){
                        dfsWinningMove.copyMove(move);
                    }
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        }
    }

    private boolean containsMove (List<Move> unrankedMoves, Move m){
        for (Move move : unrankedMoves){
            if(move.equals(m)) return true;
        }
        return false;
    }

    private List<Move> rankMoves (List<Move> moves, Move killerMove) {
        List<Move> rankedMoves = new ArrayList<>(moves.size());
        List<Move> temp = new ArrayList<>();

        if (killerMove != null){
            rankedMoves.add(killerMove);
        }
        for (Move move : moves){
            if (killerMove != null) {
                if (move.equals(killerMove)) {
                    continue;
                }
            }
            if (move.capture){
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5 && move.desty >= 2 && move.desty <= 5){ //destination is centre of board
                rankedMoves.add(move);
                continue;
            }
            if (move.destx >= 2 && move.destx <= 5){
                rankedMoves.add(move);
                continue;
            }
            if (move.desty >= 2 && move.desty <= 5){
                rankedMoves.add(move);
                continue;
            }
            temp.add(move);
        }
        rankedMoves.addAll(temp);
        return rankedMoves;
    }


    int getMaxDepthReached() {
        return maxDepthReached;
    }
}
