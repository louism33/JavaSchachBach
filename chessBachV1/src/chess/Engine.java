package chess;

import java.util.List;

public class Engine {

    private ChessBoard board;
    private Evaluator evaluator;

    private static long startTime;
    private static long allocated;
    private int increment;
    private boolean startingBoard;

    private static final String programName = "Bach Version One";

    public String getName() {
        return programName;
    }

    public void newGame(int time, int inc) {
        increment = inc;
        board = new ChessBoard();
        this.startingBoard = true;
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
        return (int) t;
    }

    public Move computeMove(int timeleft, int optime) {
        startTime = System.currentTimeMillis();
        allocated = allocateTime(timeleft, optime);

        // force a e2e4 or d2d4 start
        if (this.startingBoard){
            this.startingBoard = false;
            Move move = (Math.random() < 0.5) ? ((List<Move>) board.generateMoves()).get(8)
                    : ((List<Move>) board.generateMoves()).get(10);
            return move;
        }

        return search(startTime);
    }

    Move search(long startTime){
        ChessTree<ChessBoard> tree = new ChessTree<>(board);
        tree.findBestMoveInCertainTime(startTime);
        return tree.bestMove();
    }

}
