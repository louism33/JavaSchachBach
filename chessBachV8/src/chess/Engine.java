package chess;

import java.util.List;

public class Engine {

    private ChessBoard board;

    private static long startTime, allocated;
    private int increment;

    private static final String programName = "Bach Version Eight";

    String getName() {
        return programName;
    }

    void newGame(int time, int inc) {
        increment = inc;
        board = new ChessBoard();
    }

    void applyMove(Move m) {
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

    Move computeMove(int timeleft, int optime) {
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

    Move searchGraphics(ChessBoard board, long startTime, long timeLimitMillis){
        this.board = board;
        return search(startTime, timeLimitMillis);
    }

    Move search(long startTime, long timeLimitMillis){
        String timeInfo = String.format("I will spend %d milliseconds looking for a move.", timeLimitMillis);
        System.out.println(timeInfo);

        Move mostDesirableMove = SearchIterativeDeepener.expandBoard(board, startTime, timeLimitMillis);

        System.out.println("I will play: " + mostDesirableMove + ".");
        return mostDesirableMove;
    }
}
