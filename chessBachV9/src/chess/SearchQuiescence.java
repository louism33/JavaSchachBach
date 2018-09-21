package chess;

import java.util.ArrayList;
import java.util.List;

class SearchQuiescence {

    private static int numberOfRegularEvals = 0;
    private static int numberOfQuiescentEvals = 0;
    private static int QCounter = 0;

    static int getNumberOfRegularEvals() {
        return numberOfRegularEvals;
    }

    static int getNumberOfQuiescentEvals() {
        return numberOfQuiescentEvals;
    }

    public static int getQCounter() {
        return QCounter;
    }

    static int quiescentSearchOrganiser (ChessBoard board, int originalPlayer,
                                         int alpha, int beta,
                                         long startTime, long timeLimitMillis) {

        if (quietNode(board)) {
            numberOfRegularEvals++;
            return Evaluator.eval(board);
        } else {
            return quiescentSearch(board, originalPlayer, alpha, beta, startTime, timeLimitMillis);
        }
    }


    private static int quiescentSearch(ChessBoard board, int originalPlayer, int alpha, int beta,
                                       long startTime, long timeLimitMillis){

        int score = Evaluator.eval(board);
        numberOfQuiescentEvals++;

        if (score >= beta) {
            return beta;
        }

        if (alpha < score) {
            alpha = score;
        }

        List<Move> allDangerousMoves = allCaptureMoves(board);
        allDangerousMoves.addAll(allCheckMoves(board));

        List<Move> allOrderedDangerousMoves = MoveOrganiser.organiseMovesForQuiescent(board, allDangerousMoves);

        int totalDangerousMoves = allOrderedDangerousMoves.size();

        boolean timeUp = false;

        for (int m = 0; m < totalDangerousMoves; m++) {

            if (!timeUp){
                QCounter++;

                Move move = allOrderedDangerousMoves.get(m);

                ChessBoard childBoard = new ChessBoard(board);
                childBoard.makeMove(move);

                int value = -quiescentSearch(childBoard, 1 - originalPlayer,
                        -beta, -alpha, startTime, timeLimitMillis);

                if (value >= beta) return beta;
                if (value > alpha) {
                    alpha = value;
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


    private static int numberOfCaptureMoves (ChessBoard board){
        return allCaptureMoves(board).size();
    }

    private static List<Move> allCaptureMoves (ChessBoard board){
        List<Move> allMoves = board.generateMoves();
        List<Move> allCaptureMoves = new ArrayList<>();
        for (Move move : allMoves){
            if (move.capture){
                allCaptureMoves.add(move);
            }
        }
        return allCaptureMoves;
    }

    private static List<Move> allCheckMoves(ChessBoard board){
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

    private static boolean quietNode (ChessBoard board){
        if (board.inCheck()) return false;
        if (numberOfCaptureMoves(board) > 0) return false;
        if (allCheckMoves(board).size() > 0) return false;
        return true;
    }

}
