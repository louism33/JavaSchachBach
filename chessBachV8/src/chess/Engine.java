package chess;

import java.util.List;

public class Engine {

    private ChessBoard board;
    private IterativeDeepener iterativeDeepener;

    private static long startTime, allocated;
    private int increment;

    private static final String programName = "Bach Version Eight";

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
        t = 100;
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
        iterativeDeepener = new IterativeDeepener();
        Move mostDesirableMove = iterativeDeepener.expandBoard(board, startTime, timeLimitMillis);
        long t1 = System.currentTimeMillis();
        if (timeDetails) {
            long z = (t1 - startTime);
            System.out.println("Max Depth Reached: " + iterativeDeepener.getMaxDepthReached());
            System.out.println("Time: " + z + " millis, " + ((z)/1000) + " seconds");
        }
        return mostDesirableMove;
    }


}
