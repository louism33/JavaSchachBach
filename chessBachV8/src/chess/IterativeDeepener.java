package chess;

import java.util.ArrayList;
import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove, enemyKillerMove;

    private Evaluator evaluator;
    private final int MATE = Evaluator.MATE;

    Move expandBoard(ChessBoard board, long startTime, long timeLimitMillis){
        dfsWinningMove = ((List<Move>) board.generateMoves()).get(0);
        enemyKillerMove = new Move();
        evaluator = new Evaluator();
        Move bestMove = iterativeDeepeningSearchBoard(board, startTime, timeLimitMillis);
        return bestMove;
    }


    // TODO: in v8, quiescence and (Null Move) Pruning

    private Move iterativeDeepeningSearchBoard(ChessBoard board, long startTime,
                                               long timeLimitMillis) {
        Move bestMove = new Move();
        boolean timeUp = false;
        int depthToSearchTo = 1;
        int score, previousScore = 0;
        while (!timeUp) {

            score = aspirationSearch(board, depthToSearchTo, previousScore,
                    startTime, timeLimitMillis);
            previousScore = score;




            bestMove.copyMove(dfsWinningMove);
            System.out.println("--- " + depthToSearchTo);
            System.out.println("DFS: "+ dfsWinningMove +" "+score);
            System.out.println("EKM: " + enemyKillerMove);

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

    private int aspirationSearch(ChessBoard board, int depthToSearchTo, int aspirationScore,
                                 long startTime, long timeLimitMillis){

        int firstWindow = 150, windowDelta = 250;
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

            score = principleVariationSearch(board, board, board.getTurn(),
                    depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                timeUp = true;
            }

            if (score <= alpha){
                System.out.println("out of window : alpha");
                alpha = alpha - windowDelta;
            }
            else if (score >= beta){
                System.out.println("out of window : beta");
                beta = beta + windowDelta;
            }
            else {
                break;
            }

        }

        return score;

    }


    private int principleVariationSearch(ChessBoard motherBoard, ChessBoard board,
                                         int originalPlayer, int howDeep,
                                         int alpha, int beta, long startTime, long timeLimitMillis) {
        if (howDeep <= 1) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            int score = evaluator.eval(board) * col;
            return (quietNode(board) ? score : quiescentSearch(board, alpha, beta,
                    startTime, timeLimitMillis));
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
        }
        return alpha;
    }

    private boolean quietNode (ChessBoard board){
        if (board.inCheck()) return false;
        if (numberOfCaptureMoves(board) > 0) return false;
        if (allCheckMoves(board).size() > 0) return false;
        return true;
    }









    public static void main (String[] args){
        IterativeDeepener i = new IterativeDeepener();
    }

    IterativeDeepener(){
        ChessBoard b = new ChessBoard();
        Move m = new Move("g2g4");
        b.makeMove(m);
        Move m2 = new Move("f7f5");
        b.makeMove(m2);

//        Move m3 = new Move("d4e5");
//        b.makeMove(m3);

        evaluator = new Evaluator();

        System.out.println("Current of board is: " + evaluator.eval(b));





        int alpha = MATE + 1;
        int beta = - MATE - 1;
        long startTime = System.currentTimeMillis();
        long timeLimitMillis = 100;

        int plop = quiescentSearch(b, alpha, beta, startTime, timeLimitMillis);
        System.out.println(b);

        System.out.println("Score: " + plop);

//        Move move = expandBoard(b, startTime, timeLimitMillis);
//        System.out.println(move);




    }


    private int quiescentSearch(ChessBoard board, int alpha, int beta,
                                long startTime, long timeLimitMillis){

        if (quietNode(board)) return 666;

        int maxDepthToSearchTo = 20;
        int score = 0;
        int finalScore = 0;
        int originalPlayer = board.getTurn();
        for (int d = 1; d <= maxDepthToSearchTo; d++) {
            System.out.println("___________________________________________________ "+ d);
            score = quiescentSearchHelper(board, originalPlayer, d, alpha, beta,
                    startTime, timeLimitMillis);

            finalScore = score;

            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                break;
            }
        }

        return finalScore;
    }






    private int quiescentSearchHelper(ChessBoard board, int originalPlayer,
                                      int howDeep,  int alpha, int beta,
                                      long startTime, long timeLimitMillis) {

        List<Move> allDangerousMoves = allCaptureMoves(board);
        allDangerousMoves.addAll(allCheckMoves(board));
        int totalDangerousMoves = allDangerousMoves.size();
        System.out.println("The number of Dangerous moves is: " + totalDangerousMoves);


        if (howDeep <= 1) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            return evaluator.eval(board) * col;
        }

        if (quietNode(board)) {
            int col = (board.getTurn() == originalPlayer) ? 1 : -1;
            return evaluator.eval(board) * col;
        }

        int value = MATE + 1;

        for (int m = 0; m < totalDangerousMoves; m++) {
            long currentTime = System.currentTimeMillis();
            long timeLeft = startTime + timeLimitMillis - currentTime;
            if (timeLeft < 0) {
                break;
            }

            Move move = allDangerousMoves.get(m);
            System.out.println(move);
            ChessBoard childBoard = new ChessBoard(board);
            childBoard.makeMove(move);


            if (m == 0) {
                value = Math.max(value,
                        -quiescentSearchHelper(childBoard,
                                1 - originalPlayer, howDeep - 1,
                                -beta, -alpha, startTime, timeLimitMillis));
            } else {
                value = Math.max(value,
                        -quiescentSearchHelper(childBoard,
                                1 - originalPlayer, howDeep - 1,
                                -alpha - 1, -alpha, startTime, timeLimitMillis));
                if (value > alpha && value < beta) {
                    value = Math.max(value,
                            -quiescentSearchHelper(childBoard,
                                    1 - originalPlayer, howDeep - 1,
                                    -beta, -value, startTime, timeLimitMillis));
                }
            }
            if (value > alpha) {
                alpha = value;
            }
            if (alpha >= beta) {
                break;
            }

        }
        return alpha;
    }




    private int numberOfCaptureMoves (ChessBoard board){
        return allCaptureMoves(board).size();
    }
    private List<Move> allCaptureMoves (ChessBoard board){
        List<Move> allMoves = board.generateMoves();
        List<Move> allCaptureMoves = new ArrayList<>();
        for (Move move : allMoves){
            if (move.capture){
                allCaptureMoves.add(move);
            }
        }
        return allCaptureMoves;
    }

    private List<Move> allCheckMoves(ChessBoard board){
        List<Move> allMoves = board.generateMoves();
        List<Move> allCheckMoves = new ArrayList<>();

        for (Move move : allMoves){
            ChessBoard b = new ChessBoard(board);
            b.makeMove(move);
            if (b.inCheck()){
                allCheckMoves.add(move);
            }
        }
        return allCheckMoves;
    }

    int getMaxDepthReached() {
        return maxDepthReached;
    }
}
