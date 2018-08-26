package chess;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Engine {

    private ChessBoard board;
    private Evaluator evaluator;

    private static long startTime;
    private static long allocated;
    @SuppressWarnings("unused")
    private int initialTime, increment;

    private static final String programName = "AlphaNoob";

    static boolean earlyAbort;

    public String getName() {
        return programName;
    }

    public void newGame(int time, int inc) {
        initialTime = time;
        increment = inc;
        board = new ChessBoard();
    }

    public void applyMove(Move m) {
        board.makeMove(m);
    }

    public ChessBoard getBoard() {
        return board;
    }

	/* -------------------------------------------------------------
       Completed Private Methods Feel free to change these, if you
       want, but it's not necessary.
       -------------------------------------------------------------*/

    /** This method computes and returns an amount of time to allocate
     to the current move.  It takes as parameters my time left, and
     the opponent's time left.  However this implementation only
     makes use of my time left, and the increment (increment).
     All times are in milliseconds.

     The way it computes this is that it assumes the game is going
     to last 30 more moves, so it allocates 1/30th of the remaining
     time, plus the increment, to the current move.
     */
    private int allocateTime(int timeleft, int optime) {
        double t = increment + timeleft/30.0;
        if (t > timeleft) t = .9*timeleft;
        return (int) t;
    }

    static boolean timeIsAConcern = false;

    static boolean timeup() {
        if (timeIsAConcern) {
            if ((System.currentTimeMillis()-startTime) > allocated) {
                //			earlyAbort = true;
                System.out.println("STOP!");
                return true;
            }
        }
        return false;
    }

    public Move computeMove(int timeleft, int optime) {
        startTime = System.currentTimeMillis();
        allocated = allocateTime(timeleft, optime);


        return search2(startTime, allocated);
    }







    Move search2(long startTime, long allocated){

        ChessTree<ChessBoard> tree = new ChessTree<>(board);

        tree.findBestMoveInCertainTime(startTime, allocated+8000);

        return tree.bestMove();
    }




    /*
     *
     *
     * TODO: Chess things:
     * double bishop bonus, pk4 recommendation
     * get better position scores
     * alpha beta
     * repetition table
     * move ordering
     * quiescent search
     * pruning
     * end game boards
     * start game boards
     * save the boards that got made ?
     *
     *
     * TODO: Programming things:
     *
     * remove dependency on hard coding depth for purposes of min and max
     * remove everything static. May help when I simulate games between engines.
     * do not wait for all board to have been generated,
     * 			once desired depth (?) reached for a move, send it to eval
     * MultiThread, both in general, one thread for each list generated from possible moves
     * create this into an application, that is "double clicked" from desktop
     * GUI, if possible hack this into already existing.
     * use SOLID or similar principles to clean up
     * remove obsolete classes.
     * Optimise for speed (arrays vs lists)
     * memory control within application
     * possibly write to file
     * memory controls / tricks
     *
     *
     */

//    private Move search() {
//
//        MasterList masterList = makeListOfBoards(board);
//
//        Move selectedMove = selectMove(masterList);
//
//        return selectedMove;
//    }
//
//    private MasterList makeListOfBoards(ChessBoard board) {
//        long t1 = System.currentTimeMillis();
//
//        MasterList masterList = new MasterList(this, board);
//
//        masterList.developMasterList(allocated);
//
//        long t2 = System.currentTimeMillis();
//        System.out.println("\nTime to create MasterList: "+ ((t2 - t1)) +" millis\n" );
//        System.out.println("Depth: " + masterList.getDepth());
//        return masterList;
//    }
//
//    private Move selectMove(MasterList masterList) {
//        Evaluator evaluator = new Evaluator(masterList);
//
//        long t3 = System.currentTimeMillis();
//
//        Move bestMove = evaluator.selectBestMove(masterList);
//
//        long t4 = System.currentTimeMillis();
//        System.out.println("\nTime to create MasterList: "+ ((t4 - t3)) +" millis\n" );
//
//        return bestMove;
//    }






    /* Implement alpha beta search here.  You'll probably want to
       modify the parameters and/or return values.  Some way is needed
       to have it send back to the caller (at least at top level)
       which move was the best.  Also, it may be useful for it to know
       if this is a toplevel call (i.e. from search()) for example,
       because if there was just one legal move, then it could return
       without waiting to actually compute a score for that position.
       This is not true lower down in the tree.
     */




    private int alpha_beta(ChessBoard board, int depth, int alpha, int beta) {
        System.out.println("-----start alpha_beta()-----");

        System.out.println("-----end alpha_beta()-----");
        return 0;
    }







    private Move randomMove() {
        System.out.println("-----start randomMove()-----");
		/* If there is at least one capture move, choose one of them at random,
	   otherwise just move randomly. */
        Random rand = new Random();
        List moveList = board.generateMoves();
        if (moveList.size() == 0) return null;  // if the game is over, just return null

        Move [] moveArray = (Move[]) moveList.toArray(new Move[0]);
        Move [] captureArray = new Move[moveArray.length];
        int capCount = 0;
        for (int i=0; i<moveArray.length; i++) if (moveArray[i].capture) captureArray[capCount++] = moveArray[i];


        if (capCount > 0) return captureArray[rand.nextInt(capCount)];

        System.out.println("-----end randomMove()-----");
        return moveArray[rand.nextInt(moveArray.length)];
    }



}
