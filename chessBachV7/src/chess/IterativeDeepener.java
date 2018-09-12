package chess;

import java.util.ArrayList;
import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove;

    private Evaluator evalutator;

//    public static void main (String[] args){
//        IterativeDeepener i = new IterativeDeepener();
//    }
//
//    IterativeDeepener(){
//        ChessBoard b = new ChessBoard();
//        long st = System.currentTimeMillis();
//
//
//        Move m = expandBoard(b, st, 1000000);
//
//        long et = System.currentTimeMillis();
//
//        System.out.println("Time: " +(et - st) + " millis, " + ((et - st)/1000) + " seconds");
//    }


    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);

        evalutator = new Evaluator();

        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);

        return bestMove;
    }


    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime, long timeLimitMillis) {

        Move bestMove = new Move();
        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE + 2; // beware of integer overflow!
        int beta = Integer.MAX_VALUE;
        int depthToSearchTo = 1;
        int aspiration = 0;

        while (!timeUp && depthToSearchTo <= 7) {

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                timeUp = true;
            }


            int player = board.getTurn();

//            if (depthToSearchTo > 1){
//                alpha = aspiration - 30;
//                beta = aspiration + 30;
//            }
//            System.out.println(aspiration);

            int score = principleVariationSearch(board, board, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            aspiration = score;

            bestMove.copyMove(dfsWinningMove);

            System.out.println(bestMove +"  "+ score );
            System.out.println("----------------");

            int MATE = Evaluator.MATE;
            if (score == MATE){
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

        int value = Integer.MIN_VALUE + 2;

        List<Move> unrankedMoves = board.generateMoves();
        List<Move> moves;

        if (containsMove(unrankedMoves, dfsWinningMove)) {
            moves = rankMoves(unrankedMoves, dfsWinningMove);
        } else {
            moves = rankMoves(unrankedMoves, null);
        }

//        moves=unrankedMoves;

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
            if (m == 0) { //first move, if well ordered should be the best move
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
