package chess;

import chess.ChessBoard.SquareDesc;

public class Evaluator {

    private static final int WHITE = ChessBoard.WHITE;

    private static final int KNIGHT = ChessBoard.KNIGHT;
    private static final int BISHOP = ChessBoard.BISHOP;
    private static final int ROOK   = ChessBoard.ROOK;
    private static final int QUEEN  = ChessBoard.QUEEN;
    private static final int KING   = ChessBoard.KING;
    private static final int PAWN   = ChessBoard.PAWN;
    private static final int EMPTY  = ChessBoard.EMPTY;

    private static final int MATE = -1000000;

    public Evaluator() {
    }

    public int eval(ChessBoard board) {
        if (isInMate(board)) {
            return MATE;
        }
        if (isStalemate(board)) {
            return 0;
        }
        int myTurn = board.getTurn();
        int enemyTurn = 1 - myTurn;

        int maxMyPieces = board.getPieces(myTurn).length;
        SquareDesc[] myPieces = board.getPieces(myTurn);
        int maxEnemyPieces = board.getPieces(enemyTurn).length;
        SquareDesc[] enemyPieces = board.getPieces(enemyTurn);
        int friendlyScore = 0;
        int enemyScore = 0;
        int boardScore;

        //very basic test to work out if end game position scores should be used
        boolean endGame = maxMyPieces < 8;

        for (int p = 0; p < maxMyPieces; p ++) {
            SquareDesc piece = myPieces[p];
            int pieceTempScore = pieceValue[piece.type];
            int pieceFinalScore = pieceTempScore * locationValue(piece, true, myTurn, endGame);
            friendlyScore += pieceFinalScore;
        }

        for (int p = 0; p < maxEnemyPieces; p ++) {
            SquareDesc piece = enemyPieces[p];
            int pieceTempScore = pieceValue[piece.type];
            int pieceFinalScore = pieceTempScore * locationValue(piece, false, myTurn, endGame);
            enemyScore += pieceFinalScore;
        }
        boardScore = friendlyScore - enemyScore;

        return boardScore;
    }

    boolean isStalemate (ChessBoard b) {
        Move[] moveArray;
        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0 && !b.inCheck()) {
            return true;
        }
        return false;
    }

    boolean isInMate (ChessBoard b){
        Move[] moveArray;
        moveArray = (Move[]) b.generateMoves().toArray(new Move[0]);
        if (moveArray.length == 0 && b.inCheck()) {
            return true;
        }
        return false;
    }

    private static int locationValue(SquareDesc piece, boolean friendly, int currentPlayer, boolean endGame) {
        int ycoord;
        if (currentPlayer == WHITE) {
            ycoord = friendly ? piece.y : (7-piece.y);
        }
        else {
            ycoord = friendly ? (7-piece.y) : piece.y;
        }
        ycoord *= 8;
        if (piece.type == PAWN) {
            int loc = ycoord + piece.x;
            return pawnpos[loc];
        }
        else if (piece.type == KNIGHT) {
            int loc = ycoord + piece.x;
            return knightpos[loc];
        }
        else if (piece.type == BISHOP) {
            int loc = ycoord + piece.x;
            return bishoppos[loc];
        }
        else if (piece.type == ROOK) {
            int loc = ycoord + piece.x;
            return rookpos[loc];
        }
        else if (piece.type == QUEEN) {
            int loc = ycoord + piece.x;
            return queenpos[loc];
        }
        else if (piece.type == KING && endGame) {
            int loc = ycoord + piece.x;
            return kingposend[loc];
        }
        else if (piece.type == KING) {
            int loc = ycoord + piece.x;
            return kingposstart[loc];
        }
        else return 1;
    }

    private static int[] pieceValue = new int[7];
    static {
        pieceValue[PAWN]   = 10;
        pieceValue[KNIGHT] = 30;
        pieceValue[BISHOP] = 30;
        pieceValue[ROOK]   = 50;
        pieceValue[QUEEN]  = 90;
        pieceValue[KING]   = 900;
        pieceValue[EMPTY]  = 0;
    }

    private static int bishoppos[] = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20,};

    private static int knightpos[] = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50,};

    private static int pawnpos[] = {
            0,   0,  0,  0,  0,  0,  0,  0,
            5,  10, 10,-20,-20, 10, 10,  5,
            5,  -5,-10,  0,  0,-10, -5,  5,
            0,   0,  0, 20, 20,  0,  0,  0,
            5,   5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,   0,  0,  0,  0,  0,  0,  0,};

    private static int rookpos[] =   {
            0,   0,  0,  5,  5,  0,  0,  0,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5,  10, 10, 10, 10, 10, 10,  5,
            0,   0,  0,  0,  0,  0,  0,  0};

    private static int queenpos[] =   {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -10,  5,  5,  5,  5,  5,  0,-10,
              0,  0,  5,  5,  5,  5,  0, -5,
             -5,  0,  5,  5,  5,  5,  0, -5,
            -10,  0,  5,  5,  5,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20,};

    private static int kingposstart[] =   {
             20, 30, 10,  0,  0, 10, 30, 20,
             20, 20,  0,  0,  0,  0, 20, 20,
            -10,-20,-20,-20,-20,-20,-20,-10,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30};

    private static int kingposend[] =   {
            -50,-30,-30,-30,-30,-30,-30,-50,
            -30,-30,  0,  0,  0,  0,-30,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 30, 40, 40, 30,-10,-30,
            -30,-10, 20, 30, 30, 20,-10,-30,
            -30,-20,-10,  0,  0,-10,-20,-30,
            -50,-40,-30,-20,-20,-30,-40,-50};

}