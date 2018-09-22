package chess;

import java.util.List;

class Engine {

    private ChessBoard board;

    void newGame() {
        board = new ChessBoard();
    }

    Move searchGraphics(ChessBoard board, long startTime, long timeLimitMillis){
        this.board = board;
        int numberOfMoves = board.generateMoves().size();
        if (numberOfMoves == 1){
            ((List<ChessBoard>) board.generateMoves()).get(0);
        }
        return search(startTime, timeLimitMillis);
    }

    private Move search(long startTime, long timeLimitMillis){
        String timeInfo = String.format("I will spend %d milliseconds looking for a move.", timeLimitMillis);
        System.out.println(timeInfo);

        Move mostDesirableMove = SearchIterativeDeepener.expandBoard(board, startTime, timeLimitMillis);

        System.out.println("I will play: " + mostDesirableMove + ".");
        return mostDesirableMove;
    }
}
