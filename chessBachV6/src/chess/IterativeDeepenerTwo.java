package chess;

import java.util.List;

public class IterativeDeepenerTwo {

    private int maxDepthReached;
    private Move dfsWinningMove;

    private int MATE = Evaluator.MATE;

    private Evaluator evalutator;

    public static void main (String[] args){

        IterativeDeepenerTwo idt = new IterativeDeepenerTwo();
    }

    IterativeDeepenerTwo(){
        evalutator = new Evaluator();

        ChessBoard b = new ChessBoard();

        Move blub = expandBoard(b, 0, 0);


    }


    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);

        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);

        return bestMove;
    }



    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime, long timeLimitMillis) {

        Move bestMove = new Move();
        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depthToSearchTo = 1;

        while (!timeUp){ // && depthToSearchTo <= 5) {

            long currentTime = System.currentTimeMillis();

            long timeLeft = startTime + timeLimitMillis - currentTime;

            if (timeLeft < 0) {
                timeUp = true;
            }

            int player = board.getTurn();


            int score = depthFirstSearchBoard(board, board, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            bestMove.copyMove(dfsWinningMove);

            if (score == Integer.MAX_VALUE || score == MATE){
                System.out.println("MATE FOUND");
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
            return evalutator.eval(board) * col; // delta eval
        }


        if (board.getTurn() == originalPlayer) {
            int value = Integer.MIN_VALUE;

            List<Move> moves = board.generateMoves();

//            rankMoves();

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

            int value = Integer.MIN_VALUE;

            List<Move> moves = board.generateMoves();
//            rankMoves();

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


    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
