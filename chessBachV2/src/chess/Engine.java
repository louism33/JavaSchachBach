package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Engine {

    private ChessBoard board;
    private Evaluator evaluator;
    private IterativeDeepener iterativeDeepener;

    private static long startTime, allocated;
    private int increment, maxDepthReached;

    private static final String programName = "Bach Version Three";

    public String getName() {
        return programName;
    }

    public void newGame(int time, int inc) {
        increment = inc;
        board = new ChessBoard();
    }

    public void applyMove(Move m) {
        board.makeMove(m);
    }

    public ChessBoard getBoard() {
        return board;
    }

    private int allocateTime(int timeleft, int optime) {
        double t = increment + timeleft/30.0;
        if (t > timeleft) t = .9*timeleft;

        t = 1000;

        return (int) t;
    }

    public Move computeMove(int timeleft, int optime) {
        startTime = System.currentTimeMillis();
        allocated = allocateTime(timeleft, optime);

        if (StandAlone.startingBoard){
            Move move = (Math.random() < 0.5) ? ((List<Move>) board.generateMoves()).get(8)
                    : ((List<Move>) board.generateMoves()).get(10);
            return move;
        }

        int numberOfMoves = board.generateMoves().size();

        if (numberOfMoves == 1){
            ((List<ChessBoard>) board.generateMoves()).get(0);
        }

        return search(startTime, allocated);
    }




    Move search(long startTime, long timeLimitMillis){

        boolean timeDetails = true;

        ChessTree<ChessBoard> tree = new ChessTree<>(board);
        iterativeDeepener = new IterativeDeepener();
        NegaMaxer negaMaxer = new NegaMaxer();

        iterativeDeepener.expandTree(tree, startTime, timeLimitMillis);

        long t1 = System.currentTimeMillis();
        if (timeDetails) {
            long z = (t1 - startTime);
            System.out.println("Deepening the tree took " + z + " milliseconds.");
            System.out.println("Max Depth Reached: " + iterativeDeepener.getMaxDepthReached());
        }

        Move mostDesirableMove = negaMaxer.performNegaMaxMoveSearch(tree);

        if (timeDetails) {
            long t2 = System.currentTimeMillis();
            long zz = (t2 - t1);
            System.out.println("Extracting the scores took " + zz + " milliseconds.");
            timeInfo(tree, startTime);
        }
        System.out.println(tree);
        return mostDesirableMove;
    }





    void timeInfo(ChessTree<ChessBoard> tree, long startTime){

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long totalTimeSeconds = (endTime - startTime) / 1000;
        System.out.println("The whole process took " + totalTime + " milliseconds.");

        if (totalTime > 0) {
            System.out.println("This is approximately " + (tree.totalCount() / totalTimeSeconds) + " boards per second.");
        }
    }

}
